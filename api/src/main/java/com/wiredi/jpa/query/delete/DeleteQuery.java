package com.wiredi.jpa.query.delete;

import com.wiredi.jpa.query.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record DeleteQuery(
        DeleteClause delete,
        FromClause from,
        List<JoinClause> joins,
        Optional<Predicate> where
) implements QueryElement {
    public DeleteQuery(DeleteClause delete, FromClause from,
                       List<JoinClause> joins, Optional<Predicate> where) {
        this.delete = delete;
        this.from = from;
        this.joins = Collections.unmodifiableList(joins);
        this.where = where;
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitDeleteQuery(this);
    }
}