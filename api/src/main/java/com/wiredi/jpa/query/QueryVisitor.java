package com.wiredi.jpa.query;

import com.wiredi.jpa.query.delete.DeleteClause;
import com.wiredi.jpa.query.delete.DeleteQuery;
import com.wiredi.jpa.query.select.SelectClause;
import com.wiredi.jpa.query.select.SelectQuery;
import com.wiredi.jpa.query.update.UpdateClause;
import com.wiredi.jpa.query.update.UpdateQuery;

public interface QueryVisitor<R> {
    R visitQuery(SelectQuery query);
    R visitSelect(SelectClause select);
    R visitUpdate(UpdateClause update);
    R visitDelete(DeleteClause delete);
    R visitUpdateQuery(UpdateQuery query);
    R visitDeleteQuery(DeleteQuery query);
    R visitFrom(FromClause from);
    R visitJoin(JoinClause join);
    R visitGroupBy(GroupByClause groupBy);
    R visitOrderBy(OrderByClause orderBy);
    R visitPredicate(Predicate predicate);
    R visitCompoundPredicate(CompoundPredicate predicate);
    R visitHaving(HavingClause having);
    R visitField(Field field);
    R visitFunction(FunctionCall function);
    R visitOrder(Order order);
    R visitAliased(AliasedExpression alias);
    R visitSubquery(SubqueryExpression sub);
}
