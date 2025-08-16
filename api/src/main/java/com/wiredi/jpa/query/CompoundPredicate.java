package com.wiredi.jpa.query;

public class CompoundPredicate extends Predicate {
    public final Predicate left;
    public final String op;
    public final Predicate right;
    public CompoundPredicate(Predicate left, String op, Predicate right) {
        super("(" + left.expr + " " + op + " " + right.expr + ")");
        this.left = left; this.op = op; this.right = right;
    }
    @Override
    public <R> R accept(QueryVisitor<R> visitor) { return visitor.visitCompoundPredicate(this); }
}
