package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.JoinType;
import com.wiredi.jpa.query.Predicate;
import com.wiredi.jpa.query.Source;

public interface SelectFromQueryBuilder extends SelectQueryBuilder, SelectWhereQueryBuilder, SelectGroupByQueryBuilder {
    SelectFromQueryBuilder join(Source src, JoinType type, Predicate on);

    SelectWhereQueryBuilder where(Predicate p);
}
