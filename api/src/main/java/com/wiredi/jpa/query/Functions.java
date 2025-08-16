package com.wiredi.jpa.query;

import java.util.Collections;
import java.util.Optional;

// Utility functions (in static util class)
public class Functions {
    public static FunctionCall count(Expression e) { return new FunctionCall("COUNT", Collections.singletonList(e), Optional.empty()); }
    public static FunctionCall sum(Expression e) { return new FunctionCall("SUM", Collections.singletonList(e), Optional.empty()); }
    public static FunctionCall avg(Expression e) { return new FunctionCall("AVG", Collections.singletonList(e), Optional.empty()); }
    public static FunctionCall min(Expression e) { return new FunctionCall("MIN", Collections.singletonList(e), Optional.empty()); }
    public static FunctionCall max(Expression e) { return new FunctionCall("MAX", Collections.singletonList(e), Optional.empty()); }
    public static FunctionCall rowNumber() { return new FunctionCall("ROW_NUMBER", Collections.emptyList(), Optional.empty()); }
    public static FunctionCall over(FunctionCall f, WindowSpec w) { return new FunctionCall(f.name(), f.args(), Optional.of(w)); }
}
