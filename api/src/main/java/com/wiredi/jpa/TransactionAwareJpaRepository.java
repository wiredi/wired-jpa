package com.wiredi.jpa;

import com.wiredi.jpa.tx.TransactionContext;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionAwareJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final Class<T> entityType;
    protected final TransactionContext transactionContext;

    public TransactionAwareJpaRepository(
            Class<T> entityType,
            TransactionContext transactionContext
    ) {
        this.entityType = entityType;
        this.transactionContext = transactionContext;
    }

    @Override
    public Optional<T> find(ID id) {
        return transactionContext.call(s -> {
            EntityManager entityManager = s.getEntityManager();
            return Optional.ofNullable(entityManager.find(entityType, id));
        });
    }

    @Override
    public <S extends T> S save(S entity) {
        return transactionContext.call(s -> performSave(s.getEntityManager(), entity));
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return transactionContext.call(s -> {
            List<S> result = new ArrayList<>();
            EntityManager entityManager = s.getEntityManager();
            for (S entity : entities) {
                result.add(performSave(entityManager, entity));
            }
            return result;
        });
    }

    private <S extends T> S performSave(EntityManager entityManager, S entity) {
        if (entityManager.contains(entity)) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }
    }
}
