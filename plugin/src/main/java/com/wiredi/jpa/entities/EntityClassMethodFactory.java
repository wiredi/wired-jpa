package com.wiredi.jpa.entities;

import com.squareup.javapoet.*;
import com.wiredi.compiler.domain.ClassEntity;
import com.wiredi.compiler.domain.entities.methods.StandaloneMethodFactory;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class EntityClassMethodFactory implements StandaloneMethodFactory {

    private final TypeMirror typeMirror;

    public EntityClassMethodFactory(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    @Override
    public String methodName() {
        return "entityClass";
    }

    @Override
    public void append(MethodSpec.Builder builder, ClassEntity<?> entity) {
        builder.addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), TypeName.get(typeMirror)))
                .addStatement("return $T.class", TypeName.get(typeMirror))
                .build();
    }
}
