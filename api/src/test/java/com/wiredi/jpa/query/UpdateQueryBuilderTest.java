package com.wiredi.jpa.query;

import com.wiredi.jpa.query.update.UpdateQuery;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateQueryBuilderTest {

    @Test
    public void testBasicUpdateQuery() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Field nameField = userEntity.field("name");
        Source source = new EntitySource(userEntity);
        Map<String, Expression> setExpressions = new HashMap<>();
        setExpressions.put("name", new TestExpression("'John'"));

        // Act
        UpdateQuery query = new QueryBuilder()
                .update(setExpressions)
                .from(source)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.update());
        assertEquals(setExpressions, query.update().setExpressions());
        assertNotNull(query.from());
        assertEquals(source, query.from().source());
        assertTrue(query.joins().isEmpty());
        assertFalse(query.where().isPresent());
    }

    @Test
    public void testUpdateQueryWithWhereClause() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Source source = new EntitySource(userEntity);
        Predicate predicate = new Predicate("u.id = 1");
        Map<String, Expression> setExpressions = new HashMap<>();
        setExpressions.put("active", new TestExpression("false"));

        // Act
        UpdateQuery query = new QueryBuilder()
                .update(setExpressions)
                .from(source)
                .where(predicate)
                .build();

        // Assert
        assertNotNull(query);
        assertTrue(query.where().isPresent());
        assertEquals(predicate, query.where().get());
    }

    @Test
    public void testUpdateQueryWithJoin() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate joinPredicate = new Predicate("u.id = o.userId");
        Map<String, Expression> setExpressions = new HashMap<>();
        setExpressions.put("status", new TestExpression("'INACTIVE'"));

        // Act
        UpdateQuery query = new QueryBuilder()
                .update(setExpressions)
                .from(userSource)
                .join(orderSource, JoinType.INNER, joinPredicate)
                .build();

        // Assert
        assertNotNull(query);
        assertFalse(query.joins().isEmpty());
        assertEquals(1, query.joins().size());
        assertEquals(JoinType.INNER, query.joins().getFirst().type());
        assertEquals(orderSource, query.joins().getFirst().source());
        assertEquals(joinPredicate, query.joins().getFirst().on());
    }

    @Test
    public void testCompleteUpdateQuery() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate wherePredicate = new Predicate("u.active = true");
        Predicate joinPredicate = new Predicate("u.id = o.userId");
        Map<String, Expression> setExpressions = new HashMap<>();
        setExpressions.put("name", new TestExpression("'John'"));
        setExpressions.put("email", new TestExpression("'john@example.com'"));

        // Act
        UpdateQuery query = new QueryBuilder()
                .update(setExpressions)
                .from(userSource)
                .join(orderSource, JoinType.INNER, joinPredicate)
                .where(wherePredicate)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.update());
        assertEquals(setExpressions, query.update().setExpressions());
        assertNotNull(query.from());
        assertEquals(userSource, query.from().source());
        assertFalse(query.joins().isEmpty());
        assertEquals(1, query.joins().size());
        assertEquals(JoinType.INNER, query.joins().getFirst().type());
        assertEquals(orderSource, query.joins().getFirst().source());
        assertEquals(joinPredicate, query.joins().getFirst().on());
        assertTrue(query.where().isPresent());
        assertEquals(wherePredicate, query.where().get());
    }
}
