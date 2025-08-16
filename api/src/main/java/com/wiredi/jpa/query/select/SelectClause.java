package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.Expression;
import com.wiredi.jpa.query.QueryElement;
import com.wiredi.jpa.query.QueryVisitor;

import java.util.List;

public record SelectClause(List<Expression> expressions) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitSelect(this);
    }
}