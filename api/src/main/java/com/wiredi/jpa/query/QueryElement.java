// Package: com.example.jpql.core

package com.wiredi.jpa.query;

import java.util.*;

// Base AST interface
public interface QueryElement {
    <R> R accept(QueryVisitor<R> visitor);
}

// Visitor interface

// Core Query and Clauses









// Sources: Entity or Subquery



// Expressions: Fields, Functions, Aliases

