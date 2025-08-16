package com.wiredi.jpa.query;

public record JoinClause(JoinType type, Source source, Predicate on) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitJoin(this);
    }
}