package com.wiredi.jpa.query.delete;

import com.wiredi.jpa.query.QueryElement;
import com.wiredi.jpa.query.QueryVisitor;

public record DeleteClause() implements QueryElement {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitDelete(this);
    }
}