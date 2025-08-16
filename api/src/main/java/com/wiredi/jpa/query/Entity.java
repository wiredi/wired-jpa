package com.wiredi.jpa.query;

// Entity interface
public interface Entity {
    String name();
    String alias();
    default Field field(String name) { return new Field(this, name); }
}
