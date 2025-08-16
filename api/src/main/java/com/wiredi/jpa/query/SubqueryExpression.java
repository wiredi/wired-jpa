package com.wiredi.jpa.query;

import com.wiredi.jpa.query.select.SelectQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SubqueryExpression(
        @NotNull SelectQuery query,
        @Nullable String alias
) implements Expression, Source {
    @Override
    public @NotNull String toString() {
        String result = "(" + query + ")";
        if (alias != null) {
            return result + " AS " + alias;
        } else {
            return result;
        }
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitSubquery(this);
    }
}