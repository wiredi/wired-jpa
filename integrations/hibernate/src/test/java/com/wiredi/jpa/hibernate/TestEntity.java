package com.wiredi.jpa.hibernate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "Test")
@Table(name = "test_entity")
public class TestEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    private String name;

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestEntity entity)) return false;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
