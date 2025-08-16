package com.wiredi.jpa.query;

import java.util.List;

public record WindowSpec(
        List<Expression> partitionBy,
        List<Order> orderBy
) {
}
