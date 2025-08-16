package com.wiredi.jpa.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.RollbackException;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class TransactionStatus {

    @Nullable
    private final TransactionStatus parent;

    @NotNull
    private final EntityManager entityManager;
    private final boolean ownsEntityManager;
    @Nullable
    private Savepoint savepoint;
    private boolean rollbackOnly = false;

    public TransactionStatus(@NotNull EntityManager em, @Nullable TransactionStatus parent) {
        this(em, parent, true);
    }

    public TransactionStatus(@NotNull EntityManager em) {
        this(em, null);
    }

    private TransactionStatus(@NotNull EntityManager em, @Nullable TransactionStatus parent, boolean ownsEntityManager) {
        this.entityManager = em;
        this.parent = parent;
        this.ownsEntityManager = ownsEntityManager;
        if (!ownsEntityManager) {
            try {
                Connection conn = entityManager.unwrap(Connection.class);
                this.savepoint = conn.setSavepoint();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create savepoint", e);
            }
        }
    }

    @NotNull
    public TransactionStatus nest() {
        return new TransactionStatus(entityManager, this, false);
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
        if (parent != null) {
            parent.setRollbackOnly();
        }
    }

    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    @NotNull
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public boolean isRoot() {
        return parent == null && ownsEntityManager;
    }

    @Nullable
    public TransactionStatus getParent() {
        return parent;
    }

    public boolean ownsEntityManager() {
        return ownsEntityManager;
    }

    @Nullable
    public Savepoint getSavepoint() {
        return savepoint;
    }
}
