package com.wiredi.jpa.em;

import com.wiredi.annotations.properties.PropertyBinding;

@PropertyBinding(prefix = "wiredi.jpa.entity-manager-factory")
public record EntityManagerFactoryProperties(
        Boolean maintain
) {}
