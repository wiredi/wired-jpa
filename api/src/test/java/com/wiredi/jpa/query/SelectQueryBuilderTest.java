package com.wiredi.jpa.query;

import com.wiredi.jpa.query.select.SelectQuery;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SelectQueryBuilderTest {

    @Test
    public void testBasicQueryWithSelectAndFrom() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Field idField = userEntity.field("id");
        Field nameField = userEntity.field("name");
        Source source = new EntitySource(userEntity);

        // Act
        SelectQuery query = new QueryBuilder()
                .select(idField, nameField)
                .from(source)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.select());
        assertEquals(2, query.select().expressions().size());
        assertTrue(query.select().expressions().contains(idField));
        assertTrue(query.select().expressions().contains(nameField));
        assertNotNull(query.from());
        assertEquals(source, query.from().source());
        assertTrue(query.joins().isEmpty());
        assertFalse(query.where().isPresent());
        assertFalse(query.groupBy().isPresent());
        assertFalse(query.having().isPresent());
        assertFalse(query.orderBy().isPresent());
    }

    @Test
    public void testQueryWithWhereClause() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Field idField = userEntity.field("id");
        Source source = new EntitySource(userEntity);
        Predicate predicate = new Predicate("u.id = 1");

        // Act
        SelectQuery query = new QueryBuilder()
                .select(idField)
                .from(source)
                .where(predicate)
                .build();

        // Assert
        assertNotNull(query);
        assertTrue(query.where().isPresent());
        assertEquals(predicate, query.where().get());
    }

    @Test
    public void testQueryWithJoin() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Field idField = userEntity.field("id");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate joinPredicate = new Predicate("u.id = o.userId");

        // Act
        SelectQuery query = new QueryBuilder()
                .select(idField)
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
    public void testQueryWithGroupByHavingAndOrderBy() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Field idField = userEntity.field("id");
        Field nameField = userEntity.field("name");
        Source source = new EntitySource(userEntity);
        Predicate havingPredicate = new Predicate("COUNT(u.id) > 1");
        Order order = new Order(nameField, Direction.ASC);

        // Act
        SelectQuery query = new QueryBuilder()
                .select(idField, nameField)
                .from(source)
                .groupBy(nameField)
                .having(havingPredicate)
                .orderBy(order)
                .build();

        // Assert
        assertNotNull(query);
        assertTrue(query.groupBy().isPresent());
        assertEquals(1, query.groupBy().get().expressions().size());
        assertTrue(query.groupBy().get().expressions().contains(nameField));
        assertTrue(query.having().isPresent());
        assertEquals(havingPredicate, query.having().get().predicate());
        assertTrue(query.orderBy().isPresent());
        assertEquals(1, query.orderBy().get().orders().size());
        assertTrue(query.orderBy().get().orders().contains(order));
    }

    @Test
    public void testCompleteQuery() {
        // Arrange
        Entity userEntity = new TestEntity("User", "u");
        Entity orderEntity = new TestEntity("Order", "o");
        Field idField = userEntity.field("id");
        Field nameField = userEntity.field("name");
        Source userSource = new EntitySource(userEntity);
        Source orderSource = new EntitySource(orderEntity);
        Predicate wherePredicate = new Predicate("u.active = true");
        Predicate joinPredicate = new Predicate("u.id = o.userId");
        Predicate havingPredicate = new Predicate("COUNT(o.id) > 1");
        Order order = new Order(nameField, Direction.ASC);

        // Act
        SelectQuery query = new QueryBuilder()
                .select(idField, nameField)
                .from(userSource)
                .join(orderSource, JoinType.INNER, joinPredicate)
                .where(wherePredicate)
                .groupBy(nameField)
                .having(havingPredicate)
                .orderBy(order)
                .build();

        // Assert
        assertNotNull(query);
        assertNotNull(query.select());
        assertEquals(2, query.select().expressions().size());
        assertTrue(query.select().expressions().contains(idField));
        assertTrue(query.select().expressions().contains(nameField));
        assertNotNull(query.from());
        assertEquals(userSource, query.from().source());
        assertFalse(query.joins().isEmpty());
        assertEquals(1, query.joins().size());
        assertEquals(JoinType.INNER, query.joins().getFirst().type());
        assertEquals(orderSource, query.joins().getFirst().source());
        assertEquals(joinPredicate, query.joins().getFirst().on());
        assertTrue(query.where().isPresent());
        assertEquals(wherePredicate, query.where().get());
        assertTrue(query.groupBy().isPresent());
        assertEquals(1, query.groupBy().get().expressions().size());
        assertTrue(query.groupBy().get().expressions().contains(nameField));
        assertTrue(query.having().isPresent());
        assertEquals(havingPredicate, query.having().get().predicate());
        assertTrue(query.orderBy().isPresent());
        assertEquals(1, query.orderBy().get().orders().size());
        assertTrue(query.orderBy().get().orders().contains(order));
    }
}