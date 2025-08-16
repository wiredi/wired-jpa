package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SelectQuery(
        SelectClause select,
        FromClause from,
        List<JoinClause> joins,
        Optional<Predicate> where,
        Optional<GroupByClause> groupBy,
        Optional<HavingClause> having,
        Optional<OrderByClause> orderBy
) implements QueryElement {
    public SelectQuery(SelectClause select, FromClause from,
                       List<JoinClause> joins, Optional<Predicate> where,
                       Optional<GroupByClause> groupBy, Optional<HavingClause> having,
                       Optional<OrderByClause> orderBy) {
        this.select = select;
        this.from = from;
        this.joins = Collections.unmodifiableList(joins);
        this.where = where;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitQuery(this);
    }
}