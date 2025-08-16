package com.wiredi.jpa.query.update;

import com.wiredi.jpa.query.Expression;
import com.wiredi.jpa.query.QueryElement;
import com.wiredi.jpa.query.QueryVisitor;

import java.util.Map;

public record UpdateClause(Map<String, Expression> setExpressions) implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitUpdate(this);
    }
}