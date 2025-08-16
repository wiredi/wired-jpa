package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.Order;
import com.wiredi.jpa.query.Predicate;

public interface SelectGroupByQueryBuilder extends SelectQueryBuilder {
    SelectGroupByQueryBuilder having(Predicate p);

    SelectQueryBuilder orderBy(Order... orders);
}
