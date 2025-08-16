package com.wiredi.jpa.repository;

import com.squareup.javapoet.*;
import com.wiredi.annotations.Wire;
import com.wiredi.compiler.domain.AbstractClassEntity;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.jpa.TransactionAwareJpaRepository;
import com.wiredi.jpa.tx.TransactionContext;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class RepositoryImplementationClassEntity extends AbstractClassEntity<RepositoryImplementationClassEntity> {

    public RepositoryImplementationClassEntity(TypeElement repository, Annotations annotations) {
        super(repository, repository.asType(), repository.getSimpleName().toString() + "Impl", annotations);
    }

    @Override
    protected TypeSpec.Builder createBuilder(TypeMirror type) {
        return TypeSpec.classBuilder(className())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Wire.class)
                        .addMember("proxy", "false")
                        .build());
    }

    public RepositoryImplementationClassEntity addSuperclass(TypeMirror entityType, TypeMirror idType) {
        builder.superclass(ParameterizedTypeName.get(ClassName.get(TransactionAwareJpaRepository.class), TypeName.get(entityType), TypeName.get(idType)));
        return this;
    }

    public RepositoryImplementationClassEntity setConstructor(TypeMirror entityType) {
        builder.addMethod(MethodSpec.constructorBuilder()
                        .addParameter(TransactionContext.class, "transactionContext")
                        .addStatement("super($T.class, transactionContext)", entityType)
                        .addModifiers(Modifier.PUBLIC)
                .build());
        return this;
    }
}
