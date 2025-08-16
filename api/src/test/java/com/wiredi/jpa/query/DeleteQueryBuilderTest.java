package com.wiredi.jpa.query;

import com.wiredi.jpa.query.delete.DeleteQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteQueryBuilderTest {

    @Test
    public void testBasicDeleteQuery() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Source source = new EntitySource(userEntity);

        // Act
        DeleteQuery query = new QueryBuilder()
                .delete()
                .from(source)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.delete());
        assertNotNull(query.from());
        assertEquals(source, query.from().source());
        assertTrue(query.joins().isEmpty());
        assertFalse(query.where().isPresent());
    }

    @Test
    public void testDeleteQueryWithWhereClause() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Source source = new EntitySource(userEntity);
        Predicate predicate = new Predicate("u.id = 1");

        // Act
        DeleteQuery query = new QueryBuilder()
                .delete()
                .from(source)
                .where(predicate)
                .build();

        // Assert
        assertNotNull(query);
        assertTrue(query.where().isPresent());
        assertEquals(predicate, query.where().get());
    }

    @Test
    public void testDeleteQueryWithJoin() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate joinPredicate = new Predicate("u.id = o.userId");

        // Act
        DeleteQuery query = new QueryBuilder()
                .delete()
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
    public void testCompleteDeleteQuery() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate wherePredicate = new Predicate("u.active = true");
        Predicate joinPredicate = new Predicate("u.id = o.userId");

        // Act
        DeleteQuery query = new QueryBuilder()
                .delete()
                .from(userSource)
                .join(orderSource, JoinType.INNER, joinPredicate)
                .where(wherePredicate)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.delete());
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