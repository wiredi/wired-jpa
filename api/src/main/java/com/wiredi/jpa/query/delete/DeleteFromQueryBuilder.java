package com.wiredi.jpa.query.delete;

import com.wiredi.jpa.query.JoinType;
import com.wiredi.jpa.query.Predicate;
import com.wiredi.jpa.query.Source;

public interface DeleteFromQueryBuilder extends DeleteQueryBuilder, DeleteWhereQueryBuilder {
    DeleteFromQueryBuilder join(Source src, JoinType type, Predicate on);

    DeleteWhereQueryBuilder where(Predicate p);
}