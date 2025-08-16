package com.wiredi.jpa.query;

public record Field(Entity entity, String name) implements Expression {
    public String getExpression() {
        return entity.alias() + "." + name;
    }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitField(this);
    }
}
