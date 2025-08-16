package com.wiredi.jpa.query;

public record HavingClause(Predicate predicate) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitHaving(this);
    }
}