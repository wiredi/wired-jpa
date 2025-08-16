package com.wiredi.jpa.query.update;

import com.wiredi.jpa.query.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record UpdateQuery(
        UpdateClause update,
        FromClause from,
        List<JoinClause> joins,
        Optional<Predicate> where
) implements QueryElement {
    public UpdateQuery(UpdateClause update, FromClause from,
                       List<JoinClause> joins, Optional<Predicate> where) {
        this.update = update;
        this.from = from;
        this.joins = Collections.unmodifiableList(joins);
        this.where = where;
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitUpdateQuery(this);
    }
}