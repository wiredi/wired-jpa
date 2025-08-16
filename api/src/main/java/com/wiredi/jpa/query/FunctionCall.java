package com.wiredi.jpa.query;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record FunctionCall(
        @NotNull String name,
        @NotNull List<Expression> args,
        Optional<WindowSpec> window
) implements Expression {
    @Override
    public <R> R accept(QueryVisitor<R> visitor) {
        return visitor.visitFunction(this);
    }
}
