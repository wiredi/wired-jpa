package com.wiredi.jpa.entities;

import com.squareup.javapoet.*;
import com.wiredi.annotations.Wire;
import com.wiredi.compiler.domain.AbstractClassEntity;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.jpa.em.EntityMetadataProvider;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class EntityMetadataProviderClassEntity extends AbstractClassEntity<EntityMetadataProviderClassEntity>  {

    public EntityMetadataProviderClassEntity(@NotNull TypeElement entity, Annotations annotations) {
        super(entity, entity.asType(), entity.getSimpleName().toString() + "MetadataProvider", annotations);
        addSource(entity);
        setPackageOf(entity);
    }

    @Override
    protected TypeSpec.Builder createBuilder(TypeMirror type) {
        return TypeSpec.classBuilder(className())
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityMetadataProvider.class), TypeName.get(type)))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(
                        AnnotationSpec.builder(Wire.class)
                                .addMember("proxy", "false")
                                .build()
                );
    }
}
