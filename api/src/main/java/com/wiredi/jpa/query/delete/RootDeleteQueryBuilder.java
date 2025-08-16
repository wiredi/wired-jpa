package com.wiredi.jpa.query.delete;

import com.wiredi.jpa.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RootDeleteQueryBuilder implements DeleteQueryBuilder, DeleteFromQueryBuilder, DeleteWhereQueryBuilder {

    private final DeleteClause delete;
    private FromClause from;
    private final List<JoinClause> joins = new ArrayList<>();
    private Predicate where;

    public RootDeleteQueryBuilder(DeleteClause delete) {
        this.delete = delete;
    }

    @Override
    public DeleteFromQueryBuilder from(Source src) { from = new FromClause(src); return this; }
    @Override
    public DeleteFromQueryBuilder join(Source src, JoinType type, Predicate on) { joins.add(new JoinClause(type, src, on)); return this; }
    @Override
    public DeleteWhereQueryBuilder where(Predicate p) { where = p; return this; }

    @Override
    public DeleteQuery build() {
        return new DeleteQuery(delete, from, joins,
                Optional.ofNullable(where));
    }
}