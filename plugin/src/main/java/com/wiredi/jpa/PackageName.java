package com.wiredi.jpa;

import javax.lang.model.element.PackageElement;

public record PackageName(
        String value
) {

    public static PackageName of(PackageElement packageElement) {
        return new PackageName(packageElement.getQualifiedName().toString());
    }
}
