package com.wiredi.jpa.query;

public class TestExpression implements Expression {
    private final String value;

    public TestExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        // For testing purposes, we can just return null or throw an exception
        throw new UnsupportedOperationException("TestExpression is only for testing and doesn't support accept()");
    }
}