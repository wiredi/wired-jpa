package com.wiredi.jpa.query;

public record AliasedExpression(Expression expr, String alias) implements Expression {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitAliased(this);
    }
}
