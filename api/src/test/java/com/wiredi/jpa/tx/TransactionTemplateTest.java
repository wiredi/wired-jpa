package com.wiredi.jpa.tx;

import com.wiredi.jpa.tx.exception.TransactionRollbackException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionTemplateTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction entityTransaction;

    @Mock
    private Connection connection;

    @Mock
    private Savepoint savepoint;

    private TransactionContext transactionTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        transactionTemplate = new TransactionContext(entityManagerFactory);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
    }

    @Test
    void testExecute_runnable_withRequiredPropagation() {
        // Arrange
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).commit();

        // Act
        transactionTemplate.run(() -> {
            // Do something in transaction
        });

        // Assert
        verify(entityManagerFactory).createEntityManager();
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(entityTransaction, never()).rollback();
        verify(entityManager).close();
    }

    @Test
    void testExecute_runnable_withException() {
        // Arrange
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).rollback();
        RuntimeException exception = new RuntimeException("Test exception");

        // Act/Then
        assertThatThrownBy(() -> 
            transactionTemplate.run(() -> {
                throw exception;
            })
        ).hasCause(exception);

        // Assert
        verify(entityManagerFactory).createEntityManager();
        verify(entityTransaction).begin();
        verify(entityTransaction, never()).commit();
        verify(entityTransaction).rollback();
        verify(entityManager).close();
    }

    @Test
    void testExecute_supplier_withRequiredPropagation() {
        // Arrange
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).commit();
        String expectedResult = "test result";

        // Act
        String result = transactionTemplate.call(() -> expectedResult);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(entityManagerFactory).createEntityManager();
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(entityTransaction, never()).rollback();
        verify(entityManager).close();
    }

    @Test
    void testExecute_nestedTransactions() throws SQLException {
        // Arrange
        when(entityManager.unwrap(Connection.class)).thenReturn(connection);
        when(connection.setSavepoint()).thenReturn(savepoint);
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).commit();

        // Act
        transactionTemplate.run((outerStatus) -> {
            // Nested transaction
            transactionTemplate.run((innerStatus) -> {
                // Verify inner transaction is nested
                assertThat(innerStatus.getParent()).isNotNull();
                assertThat(innerStatus.isRoot()).isFalse();
                assertThat(innerStatus.ownsEntityManager()).isFalse();
                assertThat(innerStatus.getSavepoint()).isNotNull();
            });

            // Verify outer transaction is root
            assertThat(outerStatus.getParent()).isNull();
            assertThat(outerStatus.isRoot()).isTrue();
            assertThat(outerStatus.ownsEntityManager()).isTrue();
            assertThat(outerStatus.getSavepoint()).isNull();
        });

        // Assert
        verify(entityManagerFactory).createEntityManager();
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(entityTransaction, never()).rollback();
        verify(entityManager).close();
        verify(connection).setSavepoint();
    }

    @Test
    void testExecute_nestedTransactionWithRollback() throws SQLException {
        // Arrange
        when(connection.setSavepoint()).thenReturn(savepoint);
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).rollback(); // Expect rollback instead of commit
        doNothing().when(connection).rollback(any(Savepoint.class));
        RuntimeException originalException = new RuntimeException("Nested transaction exception");

        // Act
        try {
            transactionTemplate.call((outerStatus) -> {
                // Nested transaction that fails
                transactionTemplate.call((innerStatus) -> {
                        throw originalException;
                    });

                // This line should not be reached due to the exception
                return "success";
            });
            fail("Expected exception was not thrown");
        } catch (TransactionRollbackException e) {
            // In nested transactions, the exception gets wrapped multiple times
            // The outer transaction wraps the exception from the inner transaction
            Throwable cause = e.getCause();
            assertThat(cause).isEqualTo(originalException);
        }

        // Assert
        verify(entityManagerFactory).createEntityManager();
        verify(entityTransaction).begin();
        verify(entityTransaction, never()).commit();
        verify(entityTransaction).rollback(); // Verify rollback was called
        verify(entityManager).close();
        verify(connection).setSavepoint(); // Savepoint is created for the nested transaction
        verify(connection).rollback(savepoint); // Rollback to savepoint is called when the nested transaction fails
    }
}
