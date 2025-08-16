package com.wiredi.jpa.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.domain.injection.NameContext;
import com.wiredi.compiler.errors.CompositeProcessingException;
import com.wiredi.compiler.errors.ProcessingException;
import com.wiredi.compiler.logger.slf4j.MessagerContext;
import com.wiredi.compiler.processor.lang.AnnotationProcessorSubroutine;
import com.wiredi.compiler.processor.lang.ProcessingElement;
import com.wiredi.compiler.repository.CompilerRepository;
import com.wiredi.jpa.JpaRepository;
import com.wiredi.jpa.annotations.*;
import com.wiredi.jpa.repository.RepositoryImplementationClassEntity;
import com.wiredi.runtime.collections.pages.Page;
import com.wiredi.runtime.values.Value;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@AutoService(AnnotationProcessorSubroutine.class)
public class RepositoryWireProcessor implements AnnotationProcessorSubroutine {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryWireProcessor.class);
    private final Value<TypeElement> jpaRepositoryType = Value.empty();
    @Inject
    private CompilerRepository compilerRepository;
    @Inject
    private Elements elements;
    @Inject
    private Types types;
    @Inject
    private Annotations annotations;

    @Override
    public void handle(ProcessingElement processingElement) {
        Element element = processingElement.element();
        if (!(element instanceof TypeElement typeElement)) {
            throw new IllegalArgumentException("Element " + element + " is not a type element");
        }
        if (!element.getKind().isInterface()) {
            throw new IllegalArgumentException("Element " + element + " is not an interface");
        }

        Repository repositoryAnnotation = element.getAnnotation(Repository.class);
        ParameterStrategy defaultParameterStrategy;
        if (repositoryAnnotation.parameterStrategy() != ParameterStrategy.DEFAULT) {
            defaultParameterStrategy = repositoryAnnotation.parameterStrategy();
        } else {
            defaultParameterStrategy = ParameterStrategy.NAMED;
        }


        NameContext nameContext = new NameContext();
        TypeElement jpaRepositoryTypeElement = this.jpaRepositoryType.getOrSet(() -> elements.getTypeElement(JpaRepository.class.getCanonicalName()));
        RepositoryImplementationClassEntity classEntity = new RepositoryImplementationClassEntity(typeElement, annotations);
        AtomicReference<TypeMirror> entityType = new AtomicReference<>();
        typeElement.getInterfaces().forEach(it -> {
            logger.info("Interface {}", it);
            if (
                    entityType.get() == null
                            && types.isAssignable(types.erasure(typeElement.asType()), types.erasure(jpaRepositoryTypeElement.asType()))
                            && (it instanceof DeclaredType declaredType)
            ) {
                List<? extends TypeMirror> typeParameters = declaredType.getTypeArguments();
                classEntity.addSuperclass(typeParameters.get(0), typeParameters.get(1)).setConstructor(typeParameters.get(0));
                entityType.set(typeParameters.get(0));
            }
        });

        if (entityType.get() == null) {
            throw new IllegalArgumentException("Element " + typeElement + " does not implement " + JpaRepository.class.getCanonicalName());
        }

        classEntity.addInterface(TypeName.get(typeElement.asType()));

        List<Errors> errors = new ArrayList<>();
        element.getEnclosedElements()
                .stream()
                .filter(it -> it.getKind() == ElementKind.METHOD)
                .map(it -> (ExecutableElement) it)
                .forEach(method -> {
                    Annotations.getAnnotation(method, Query.class)
                            .map(queryAnnotation -> {
                                ParameterStrategy parameterStrategy = defaultParameterStrategy;
                                if (queryAnnotation.parameterStrategy() != ParameterStrategy.DEFAULT) {
                                    parameterStrategy = queryAnnotation.parameterStrategy();
                                }

                                return methodSpec(queryAnnotation, method, nameContext, TypeName.get(entityType.get()), parameterStrategy);
                            })
                            .ifPresentOrElse(
                                    methodSpec -> {
                                        classEntity.addMethod(methodSpec.methodSpec);
                                        classEntity.addField(methodSpec.fieldSpec);
                                    },
                                    () -> {
                                        if (!method.isDefault()) {
                                            errors.add(new Errors("Method " + method + " is not annotated with @Query, nor default. Unable to implement.", method));
                                        }
                                    }
                            );
                });

        if (!errors.isEmpty()) {
            throw new CompositeProcessingException(errors.stream().map(it -> new ProcessingException(it.element, it.message)).toList());
        }
        compilerRepository.save(classEntity);

    }

    @Override
    public List<Class<? extends Annotation>> targetAnnotations() {
        return List.of(Repository.class);
    }

    private QueryOverridingMethod methodSpec(
            Query query,
            ExecutableElement method,
            NameContext nameContext,
            TypeName entityType,
            ParameterStrategy parameterStrategy
    ) {
        boolean namedParameters = parameterStrategy == ParameterStrategy.NAMED;
        CodeBlock.Builder methodBody = CodeBlock.builder();
        String queryFieldName = nameContext.nextName("QUERY");

        // Check if query is modifying based on annotation or query content
        boolean isModifyingQuery = Annotations.isAnnotatedWith(method, Modifying.class) ||
                query.value().trim().toLowerCase().startsWith("insert") ||
                query.value().trim().toLowerCase().startsWith("update") ||
                query.value().trim().toLowerCase().startsWith("delete");

        // Get return type
        TypeMirror returnType = method.getReturnType();
        String returnTypeName = returnType.toString();
        boolean isVoid = returnTypeName.equals("void");
        boolean isCollection = returnTypeName.startsWith("java.util.Collection") ||
                returnTypeName.startsWith("java.util.List") ||
                returnTypeName.startsWith("java.util.Set") ||
                returnTypeName.startsWith("java.lang.Iterable");
        boolean isPage = returnTypeName.equals("com.wiredi.runtime.collections.pages.Page");

        // Start transaction
        if (isVoid) {
            methodBody.add("transactionContext.run(s -> {\n").indent();
        } else {
            methodBody.add("return transactionContext.call(s -> {\n").indent();
        }

        // Create query
        if (query.nativeQuery()) {
            methodBody.addStatement("$T query = s.getEntityManager().createNativeQuery($L)", jakarta.persistence.Query.class, queryFieldName);
        } else {
            methodBody.addStatement("$T query = s.getEntityManager().createQuery($L)", jakarta.persistence.Query.class, queryFieldName);
        }

        // Set parameters
        List<? extends VariableElement> parameters = method.getParameters();
        int parameterCount = 0;
        for (VariableElement param : parameters) {
            String paramName = param.getSimpleName().toString();
            String paramType = param.asType().toString();

            // Handle Pageable parameter specially
            if (paramType.endsWith("Pageable")) {
                methodBody.addStatement("query.setFirstResult($L.getPageSize() + ($L.getPage() + 1))", paramName);
                methodBody.addStatement("query.setMaxResults($L.getPageSize())", paramName);
            } else {
                // Regular parameter
                String parameterName = Annotations.getAnnotation(param, Parameter.class)
                        .map(Parameter::value)
                        .orElse(paramName);
                if (namedParameters) {
                    methodBody.addStatement("query.setParameter($S, $L)", parameterName, paramName);
                } else {
                    methodBody.addStatement("query.setParameter($L, $L)", parameterCount++, paramName);
                }
            }
        }

        // Execute query based on type
        if (isModifyingQuery) {
            // For modifying queries, execute update
            if (!isVoid) {
                methodBody.addStatement("int result = query.executeUpdate()");
                methodBody.addStatement("return result");
            } else {
                methodBody.addStatement("query.executeUpdate()");
            }
        } else {
            // For select queries, handle different return types
            if (isVoid) {
                MessagerContext.runNested(instance -> {
                    instance.setElement(method);
                    logger.warn("Method {} returns void. This is not recommended. Use void methods only for queries that do not select any data.", method);
                });
                methodBody.addStatement("query.getResultList()");
            } else if (isCollection) {
                methodBody.addStatement("return query.getResultList()");
            } else {
                methodBody.addStatement("$T results = query.getResultList()", ParameterizedTypeName.get(ClassName.get(List.class), entityType));
                if (isPage) {
                    // For Page return type, we need to create a Page object
                    methodBody.addStatement("long total = results.size()"); // This is simplified; in a real implementation, you'd do a count query
                    methodBody.addStatement("return new $T<>(results, total)", Page.class);
                } else {
                    // For single object return type
                    methodBody.beginControlFlow("if (results.isEmpty())");
                    methodBody.addStatement("return null");
                    methodBody.nextControlFlow("else");
                    methodBody.addStatement("return results.getFirst()");
                    methodBody.endControlFlow();
                }
            }
        }

        methodBody.unindent().add("});\n");

        return new QueryOverridingMethod(
                MethodSpec.overriding(method)
                        .addCode(methodBody.build())
                        .build(),
                FieldSpec.builder(
                                TypeName.get(String.class),
                                queryFieldName,
                                Modifier.PRIVATE,
                                Modifier.STATIC,
                                Modifier.FINAL
                        ).initializer("$S", query.value())
                        .build()
        );
    }

    record Errors(String message, Element element) {
    }

    record QueryOverridingMethod(
            MethodSpec methodSpec,
            FieldSpec fieldSpec
    ) {
    }
}
