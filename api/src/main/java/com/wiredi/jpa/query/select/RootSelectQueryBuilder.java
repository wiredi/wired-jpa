package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RootSelectQueryBuilder implements SelectQueryBuilder, SelectFromQueryBuilder, SelectWhereQueryBuilder, SelectGroupByQueryBuilder {

    private final SelectClause select;
    private FromClause from;
    private final List<JoinClause> joins = new ArrayList<>();
    private Predicate where;
    private GroupByClause groupBy;
    private HavingClause having;
    private OrderByClause orderBy;

    public RootSelectQueryBuilder(SelectClause select) {
        this.select = select;
    }

    @Override
    public SelectFromQueryBuilder from(Source src) { from = new FromClause(src); return this; }
    @Override
    public SelectFromQueryBuilder join(Source src, JoinType type, Predicate on) { joins.add(new JoinClause(type, src, on)); return this; }
    @Override
    public SelectWhereQueryBuilder where(Predicate p) { where = p; return this; }
    @Override
    public SelectGroupByQueryBuilder groupBy(Expression... exprs) { groupBy = new GroupByClause(Arrays.asList(exprs)); return this; }
    @Override
    public SelectGroupByQueryBuilder having(Predicate p) { having = new HavingClause(p); return this; }
    @Override
    public SelectQueryBuilder orderBy(Order... orders) { orderBy = new OrderByClause(Arrays.asList(orders)); return this; }

    @Override
    public SelectQuery build() {
        return new SelectQuery(select, from, joins,
                Optional.ofNullable(where),
                Optional.ofNullable(groupBy),
                Optional.ofNullable(having),
                Optional.ofNullable(orderBy));
    }

}
