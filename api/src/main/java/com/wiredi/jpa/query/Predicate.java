package com.wiredi.jpa.query;

// Predicate and CompoundPredicate
public class Predicate implements QueryElement {
    public final String expr;
    public Predicate(String expr) { this.expr = expr; }
    public Predicate and(Predicate other) { return new CompoundPredicate(this, "AND", other); }
    public Predicate or(Predicate other) { return new CompoundPredicate(this, "OR", other); }

    @Override
    public <R> R accept(QueryVisitor<R> visitor) { return visitor.visitPredicate(this); }
}
