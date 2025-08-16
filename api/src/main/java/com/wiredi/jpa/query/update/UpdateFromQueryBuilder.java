package com.wiredi.jpa.query.update;

import com.wiredi.jpa.query.JoinType;
import com.wiredi.jpa.query.Predicate;
import com.wiredi.jpa.query.Source;

public interface UpdateFromQueryBuilder extends UpdateQueryBuilder, UpdateWhereQueryBuilder {
    UpdateFromQueryBuilder join(Source src, JoinType type, Predicate on);

    UpdateWhereQueryBuilder where(Predicate p);
}