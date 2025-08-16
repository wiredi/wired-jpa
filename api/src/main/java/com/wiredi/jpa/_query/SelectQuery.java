package com.wiredi.jpa._query;

public class SelectQuery implements JPQLQuery {
    private String rawQuery;
    private String selectClause;
    private String fromClause;
    private String whereClause;
    private String groupByClause;
    private String havingClause;
    private String orderByClause;

    public SelectQuery(String rawQuery) {
        this.rawQuery = rawQuery;
        // Add parsing logic here to populate the fields
    }

    @Override
    public String getRawQuery() {
        return rawQuery;
    }

    // Getters for all the clauses
    public String getSelectClause() { return selectClause; }
    public String getFromClause() { return fromClause; }
    public String getWhereClause() { return whereClause; }
    public String getGroupByClause() { return groupByClause; }
    public String getHavingClause() { return havingClause; }
    public String getOrderByClause() { return orderByClause; }

    // You would need a parsing method here
    private void parseQuery(String query) {
        // This is a simplified example. A real parser would need more sophisticated logic
        // to handle various JPQL syntax variations, nested clauses, keywords within strings, etc.
        String upperQuery = query.toUpperCase();

        int fromIndex = upperQuery.indexOf(" FROM ");
        int whereIndex = upperQuery.indexOf(" WHERE ");
        int groupByIndex = upperQuery.indexOf(" GROUP BY ");
        int havingIndex = upperQuery.indexOf(" HAVING ");
        int orderByIndex = upperQuery.indexOf(" ORDER BY ");

        if (fromIndex != -1) {
            selectClause = query.substring(0, fromIndex).trim();

            int endIndexForFrom = whereIndex != -1 ? whereIndex :
                                  groupByIndex != -1 ? groupByIndex :
                                  havingIndex != -1 ? havingIndex :
                                  orderByIndex != -1 ? orderByIndex :
                                  query.length();
            fromClause = query.substring(fromIndex + " FROM ".length(), endIndexForFrom).trim();
        }

        if (whereIndex != -1) {
            int endIndexForWhere = groupByIndex != -1 ? groupByIndex :
                                   havingIndex != -1 ? havingIndex :
                                   orderByIndex != -1 ? orderByIndex :
                                   query.length();
            whereClause = query.substring(whereIndex + " WHERE ".length(), endIndexForWhere).trim();
        }

        if (groupByIndex != -1) {
            int endIndexForGroupBy = havingIndex != -1 ? havingIndex :
                                     orderByIndex != -1 ? orderByIndex :
                                     query.length();
            groupByClause = query.substring(groupByIndex + " GROUP BY ".length(), endIndexForGroupBy).trim();
        }

        if (havingIndex != -1) {
            int endIndexForHaving = orderByIndex != -1 ? orderByIndex : query.length();
            havingClause = query.substring(havingIndex + " HAVING ".length(), endIndexForHaving).trim();
        }

        if (orderByIndex != -1) {
            orderByClause = query.substring(orderByIndex + " ORDER BY ".length()).trim();
        }
    }
}