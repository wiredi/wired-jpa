package com.wiredi.jpa.query.select;

import com.wiredi.jpa.query.Expression;

public interface SelectWhereQueryBuilder extends SelectQueryBuilder  {
    SelectGroupByQueryBuilder groupBy(Expression... exprs);
}
