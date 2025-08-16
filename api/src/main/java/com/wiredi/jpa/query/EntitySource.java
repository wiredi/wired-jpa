package com.wiredi.jpa.query;

public record EntitySource(
        Entity entity
) implements Source {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitFrom(new FromClause(this));
    }
}