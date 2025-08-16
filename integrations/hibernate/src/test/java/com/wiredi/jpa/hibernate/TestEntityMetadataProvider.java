package com.wiredi.jpa.hibernate;

import com.wiredi.annotations.Wire;
import com.wiredi.jpa.em.EntityMetadataProvider;

@Wire
public class TestEntityMetadataProvider implements EntityMetadataProvider<TestEntity> {
    @Override
    public Class<TestEntity> entityClass() {
        return TestEntity.class;
    }
}
