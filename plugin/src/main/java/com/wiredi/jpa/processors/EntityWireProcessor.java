package com.wiredi.jpa.processors;

import com.google.auto.service.AutoService;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.processor.lang.AnnotationProcessorSubroutine;
import com.wiredi.compiler.processor.lang.ProcessingElement;
import com.wiredi.compiler.repository.CompilerRepository;
import com.wiredi.jpa.entities.EntityClassMethodFactory;
import com.wiredi.jpa.entities.EntityMetadataProviderClassEntity;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

@AutoService(AnnotationProcessorSubroutine.class)
public class EntityWireProcessor implements AnnotationProcessorSubroutine {

    private static final Logger logger = LoggerFactory.getLogger(EntityWireProcessor.class);

    @Inject
    private CompilerRepository compilerRepository;
    @Inject
    private Annotations annotations;

    @Override
    public void handle(ProcessingElement processingElement) {
        Element element = processingElement.element();
        if (!(element instanceof TypeElement typeElement)) {
            throw new IllegalArgumentException("Element " + element + " is not a type element");
        }

        compilerRepository.save(
                new EntityMetadataProviderClassEntity(typeElement, annotations)
                        .addMethod(new EntityClassMethodFactory(typeElement.asType()))
        );
    }

    @Override
    public List<Class<? extends Annotation>> targetAnnotations() {
        return List.of(Entity.class);
    }

    private record CompileTimeRelation(TypeMirror source, String field, TypeMirror target) {
    }
}
