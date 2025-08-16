package com.wiredi.jpa.query.delete;

import com.wiredi.jpa.query.Entity;
import com.wiredi.jpa.query.EntitySource;
import com.wiredi.jpa.query.Source;

public interface DeleteQueryBuilder {
    default DeleteFromQueryBuilder from(Entity entity) {
        return from(new EntitySource(entity));
    }

    DeleteFromQueryBuilder from(Source src);

    DeleteQuery build();
}