package com.wiredi.jpa;

import java.util.Map;

/**
 * Provides named queries defined in the application.
 */
public interface NamedQueryProvider {
    /**
     * @return Map from query name to HQL/JPQL string.
     */
    Map<String, String> getNamedQueries();
}