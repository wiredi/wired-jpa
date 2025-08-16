package com.wiredi.jpa.query;

// Order by element
public record Order(Expression expr, Direction direction) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitOrder(this);
    }
}
