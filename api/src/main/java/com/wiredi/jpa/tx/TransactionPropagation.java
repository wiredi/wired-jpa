package com.wiredi.jpa.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

public enum TransactionPropagation {
    SUPPORTED {
        @Override
        public TransactionStatus createTransactionStatus(@Nullable TransactionStatus currentStatus, @NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolation) {
            if (currentStatus != null) {
                return currentStatus.nest();
            } else {
                return new TransactionStatus(newEntityManager(entityManagerFactory, isolation));
            }
        }
    },
    NEVER {
        @Override
        public TransactionStatus createTransactionStatus(@Nullable TransactionStatus currentStatus, @NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolation) {
            if (currentStatus != null) {
                throw new IllegalStateException("Nested transactions are not allowed when Propagation is set to NEVER");
            }

            return new TransactionStatus(newEntityManager(entityManagerFactory, isolation));
        }
    },
    REQUIRED {
        @Override
        public TransactionStatus createTransactionStatus(@Nullable TransactionStatus currentStatus, @NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolation) {
            if (currentStatus == null) {
                throw new IllegalStateException("Transaction needs to be already opened when Propagation is set to REQUIRED");
            }

            return currentStatus.nest();
        }
    },
    REQUIRES_NEW {
        @Override
        public TransactionStatus createTransactionStatus(@Nullable TransactionStatus currentStatus, @NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolation) {
            return new TransactionStatus(newEntityManager(entityManagerFactory, isolation), currentStatus);
        }
    };

    private static EntityManager newEntityManager(@NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolationLevel) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            Connection connection = entityManager.unwrap(Connection.class);
            connection.setTransactionIsolation(isolationLevel.value());
        } catch (Exception e) {
            // If we can't unwrap the connection, just continue without setting isolation level
            // This is a workaround for the "Hibernate cannot unwrap interface java.sql.Connection" error
        }

        entityManager.setFlushMode(FlushModeType.COMMIT);
        entityManager.getTransaction().begin();
        return entityManager;
    }

    public abstract TransactionStatus createTransactionStatus(@Nullable TransactionStatus currentStatus, @NotNull EntityManagerFactory entityManagerFactory, @NotNull TransactionIsolation isolation);
}
