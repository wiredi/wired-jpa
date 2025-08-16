package com.wiredi.jpa.query;

import java.util.Collections;
import java.util.List;

public record OrderByClause(List<Order> orders) implements QueryElement {
    public OrderByClause(List<Order> orders) {
        this.orders = Collections.unmodifiableList(orders);
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitOrderBy(this);
    }
}