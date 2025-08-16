package com.wiredi.jpa.query;

import com.wiredi.jpa.query.delete.DeleteClause;
import com.wiredi.jpa.query.delete.RootDeleteQueryBuilder;
import com.wiredi.jpa.query.select.SelectClause;
import com.wiredi.jpa.query.select.RootSelectQueryBuilder;
import com.wiredi.jpa.query.update.RootUpdateQueryBuilder;
import com.wiredi.jpa.query.update.UpdateClause;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Builder for Query
public class QueryBuilder {

    public RootSelectQueryBuilder select(Expression... exprs) {
        return new RootSelectQueryBuilder(new SelectClause(Arrays.asList(exprs)));
    }

    public RootUpdateQueryBuilder update() {
        return new RootUpdateQueryBuilder(new UpdateClause(new HashMap<>()));
    }

    public RootUpdateQueryBuilder update(Map<String, Expression> setExpressions) {
        return new RootUpdateQueryBuilder(new UpdateClause(setExpressions));
    }

    public RootDeleteQueryBuilder delete() {
        return new RootDeleteQueryBuilder(new DeleteClause());
    }
}
