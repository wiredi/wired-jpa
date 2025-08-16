package com.wiredi.jpa.query.update;

import com.wiredi.jpa.query.Source;

public interface UpdateQueryBuilder {
    UpdateFromQueryBuilder from(Source src);

    UpdateQuery build();
}