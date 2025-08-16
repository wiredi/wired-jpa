package com.wiredi.jpa.tx;

import com.wiredi.jpa.tx.exception.InactiveTransactionException;
import com.wiredi.jpa.tx.exception.TransactionRollbackException;
import com.wiredi.runtime.lang.ThrowingConsumer;
import com.wiredi.runtime.lang.ThrowingFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionContext implements Transaction {

    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<TransactionStatus> statusHead = new ThreadLocal<>();

    public TransactionContext(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Nullable
    public TransactionStatus currentStatus() {
        return statusHead.get();
    }

    @Override
    public <E extends Exception> void run(@NotNull TransactionProperties properties, @NotNull ThrowingConsumer<TransactionStatus, E> consumer) throws E {
        TransactionStatus current = statusHead.get();
        TransactionStatus status = properties.propagation().createTransactionStatus(current, entityManagerFactory, properties.isolation());
        statusHead.set(status);

        try {
            consumer.accept(status);
        } catch (Exception ex) {
            throw mapStatusException(status, ex);
        } finally {
            finalBlock(status);
        }
    }

    @Override
    public <T, E extends Exception> @NotNull T call(@NotNull TransactionProperties properties, @NotNull ThrowingFunction<TransactionStatus, T, E> function) throws E {
        TransactionStatus current = statusHead.get();
        TransactionStatus status = properties.propagation().createTransactionStatus(current, entityManagerFactory, properties.isolation());
        statusHead.set(status);

        try {
            return function.apply(status);
        } catch (Exception ex) {
            throw mapStatusException(status, ex);
        } finally {
            finalBlock(status);
        }
    }

    @Override
    public <E extends Exception> void runInCurrent(@NotNull ThrowingConsumer<@NotNull TransactionStatus, E> consumer) throws E, InactiveTransactionException {
        TransactionStatus current = statusHead.get();
        if (current == null) {
            throw new InactiveTransactionException();
        }

        try {
            consumer.accept(current);
        } catch (Exception ex) {
            throw mapStatusException(current, ex);
        }
    }

    @Override
    public <T, E extends Exception> @NotNull T callInCurrent(@NotNull ThrowingFunction<TransactionStatus, T, E> function) throws E, InactiveTransactionException {
        TransactionStatus current = statusHead.get();
        if (current == null) {
            throw new InactiveTransactionException();
        }

        try {
            return function.apply(current);
        } catch (Exception ex) {
            throw mapStatusException(current, ex);
        }
    }

    private void finalBlock(TransactionStatus status) {
        try {
            if (status.ownsEntityManager()) {
                complete(status);
            } else if (status.isRollbackOnly() && status.getSavepoint() != null) {
                rollback(status);
            }
        } finally {
            resetHead(status);
        }
    }

    private TransactionRollbackException mapStatusException(TransactionStatus status, Exception ex) {
        status.setRollbackOnly();
        if (ex instanceof TransactionRollbackException t) {
            return t;
        } else {
            return new TransactionRollbackException(ex);
        }
    }

    private void resetHead(TransactionStatus status) {
        if (status.getParent() != null) {
            statusHead.set(status.getParent());
        } else {
            statusHead.remove();
        }
    }

    private void rollback(TransactionStatus status) {
        try {
            Connection conn = status.getEntityManager().unwrap(Connection.class);
            conn.rollback(status.getSavepoint());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback to savepoint", e);
        }
    }

    private void complete(TransactionStatus status) {
        try(EntityManager entityManager = status.getEntityManager()) {
            if (status.isRollbackOnly()) {
                entityManager.getTransaction().rollback();
            } else {
                entityManager.getTransaction().commit();
            }
        }
    }
}
