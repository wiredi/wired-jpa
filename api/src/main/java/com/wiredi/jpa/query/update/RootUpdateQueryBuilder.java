package com.wiredi.jpa.query.update;

import com.wiredi.jpa.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RootUpdateQueryBuilder implements UpdateQueryBuilder, UpdateFromQueryBuilder, UpdateWhereQueryBuilder {

    private final UpdateClause update;
    private FromClause from;
    private final List<JoinClause> joins = new ArrayList<>();
    private Predicate where;

    public RootUpdateQueryBuilder(UpdateClause update) {
        this.update = update;
    }

    @Override
    public UpdateFromQueryBuilder from(Source src) { from = new FromClause(src); return this; }
    @Override
    public UpdateFromQueryBuilder join(Source src, JoinType type, Predicate on) { joins.add(new JoinClause(type, src, on)); return this; }
    @Override
    public UpdateWhereQueryBuilder where(Predicate p) { where = p; return this; }

    @Override
    public UpdateQuery build() {
        return new UpdateQuery(update, from, joins,
                Optional.ofNullable(where));
    }
}