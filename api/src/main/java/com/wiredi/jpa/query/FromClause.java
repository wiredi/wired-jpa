package com.wiredi.jpa.query;

public record FromClause(
        Source source
) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitFrom(this);
    }
}