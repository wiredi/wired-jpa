package com.wiredi.jpa.tx;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionStatusTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Connection connection;

    @Mock
    private Savepoint savepoint;

    // No longer using @BeforeEach as we need to stub only in specific tests

    @Test
    void testConstructor_withEntityManager() {
        // Act
        TransactionStatus status = new TransactionStatus(entityManager);

        // Assert
        assertThat(status.getEntityManager()).isSameAs(entityManager);
        assertThat(status.getParent()).isNull();
        assertThat(status.isRoot()).isTrue();
        assertThat(status.ownsEntityManager()).isTrue();
        assertThat(status.isRollbackOnly()).isFalse();
        assertThat(status.getSavepoint()).isNull();
    }

    @Test
    void testConstructor_withEntityManagerAndParent() {
        // Arrange
        TransactionStatus parent = new TransactionStatus(entityManager);

        // Act
        TransactionStatus status = new TransactionStatus(entityManager, parent);

        // Assert
        assertThat(status.getEntityManager()).isSameAs(entityManager);
        assertThat(status.getParent()).isSameAs(parent);
        assertThat(status.isRoot()).isFalse();
        assertThat(status.ownsEntityManager()).isTrue();
        assertThat(status.isRollbackOnly()).isFalse();
        assertThat(status.getSavepoint()).isNull();
    }

    @Test
    void testNest_createsNestedTransactionStatus() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        TransactionStatus parent = new TransactionStatus(entityManager);

        // Act
        TransactionStatus nested = parent.nest();

        // Assert
        assertThat(nested.getEntityManager()).isSameAs(entityManager);
        assertThat(nested.getParent()).isSameAs(parent);
        assertThat(nested.isRoot()).isFalse();
        assertThat(nested.ownsEntityManager()).isFalse();
        assertThat(nested.isRollbackOnly()).isFalse();
        assertThat(nested.getSavepoint()).isSameAs(savepoint);

        // Verify savepoint was created
        verify(connection).setSavepoint();
    }

    @Test
    void testNest_multipleNestingLevels() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        TransactionStatus root = new TransactionStatus(entityManager);
        TransactionStatus level1 = root.nest();

        // Act
        TransactionStatus level2 = level1.nest();

        // Assert
        assertThat(level2.getParent()).isSameAs(level1);
        assertThat(level1.getParent()).isSameAs(root);
        assertThat(root.getParent()).isNull();

        assertThat(level2.isRoot()).isFalse();
        assertThat(level1.isRoot()).isFalse();
        assertThat(root.isRoot()).isTrue();

        // Verify savepoint was created twice (once for each nested level)
        verify(connection, times(2)).setSavepoint();
    }

    @Test
    void testSetRollbackOnly_propagatesToParent() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        TransactionStatus root = new TransactionStatus(entityManager);
        TransactionStatus level1 = root.nest();
        TransactionStatus level2 = level1.nest();

        // Act
        level2.setRollbackOnly();

        // Assert
        assertThat(level2.isRollbackOnly()).isTrue();
        assertThat(level1.isRollbackOnly()).isTrue();
        assertThat(root.isRollbackOnly()).isTrue();
    }

    @Test
    void testSetRollbackOnly_onRootOnly() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        TransactionStatus root = new TransactionStatus(entityManager);
        TransactionStatus level1 = root.nest();
        TransactionStatus level2 = level1.nest();

        // Act
        root.setRollbackOnly();

        // Assert
        assertThat(root.isRollbackOnly()).isTrue();
        assertThat(level1.isRollbackOnly()).isFalse();
        assertThat(level2.isRollbackOnly()).isFalse();
    }

    @Test
    void testSetRollbackOnly_onMiddleLevel() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        TransactionStatus root = new TransactionStatus(entityManager);
        TransactionStatus level1 = root.nest();
        TransactionStatus level2 = level1.nest();

        // Act
        level1.setRollbackOnly();

        // Assert
        assertThat(level1.isRollbackOnly()).isTrue();
        assertThat(root.isRollbackOnly()).isTrue();
        assertThat(level2.isRollbackOnly()).isFalse();
    }

    @Test
    void testNest_handlesExceptionWhenCreatingSavepoint() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        TransactionStatus parent = new TransactionStatus(entityManager);
        SQLException sqlException = new SQLException("Test exception");
        when(connection.setSavepoint()).thenThrow(sqlException);

        // Act/Then
        try {
            parent.nest();
        } catch (RuntimeException e) {
            assertThat(e).hasMessage("Failed to create savepoint");
            assertThat(e.getCause()).isSameAs(sqlException);
        }
    }
}
