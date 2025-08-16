package com.wiredi.jpa._query;

public class JPQLParser {

    public JPQLQuery parse(String jpql) {
        String trimmedJpql = jpql.trim();
        String upperJpql = trimmedJpql.toUpperCase();

        if (upperJpql.startsWith("SELECT")) {
            return new SelectQuery(trimmedJpql);
        } else if (upperJpql.startsWith("UPDATE") || upperJpql.startsWith("DELETE")) {
            return new UpdateDeleteQuery(trimmedJpql);
        } else {
            throw new IllegalArgumentException("Unsupported JPQL query type");
        }
    }
}