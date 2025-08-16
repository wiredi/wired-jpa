package com.wiredi.jpa._query;

public class UpdateDeleteQuery implements JPQLQuery {
    private String rawQuery;
    private String whereClause;

    public UpdateDeleteQuery(String rawQuery) {
        this.rawQuery = rawQuery;
        // Add parsing logic here
    }

    @Override
    public String getRawQuery() {
        return rawQuery;
    }

    public String getWhereClause() {
        return whereClause;
    }

    private void parseQuery(String query) {
        String upperQuery = query.toUpperCase();
        int whereIndex = upperQuery.indexOf(" WHERE ");

        if (whereIndex != -1) {
            whereClause = query.substring(whereIndex + " WHERE ".length()).trim();
        }
    }
}