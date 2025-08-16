package com.wiredi.jpa.query;

import java.util.List;

public record GroupByClause(List<Expression> expressions) implements QueryElement {

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitGroupBy(this);
    }
}