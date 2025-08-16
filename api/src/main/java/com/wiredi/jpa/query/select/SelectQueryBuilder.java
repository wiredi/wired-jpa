package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.Source;

public interface SelectQueryBuilder {
    SelectFromQueryBuilder from(Source src);

    SelectQuery build();
}
