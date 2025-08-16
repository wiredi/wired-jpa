package com.wiredi.jpa.em;

import com.wiredi.jpa.tx.TransactionContext;
import com.wiredi.jpa.tx.TransactionStatus;
import com.wiredi.runtime.values.Value;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;

public class EntityManagerProxy implements EntityManager {

    private final EntityManagerFactory fallbackFactory;
    private final Value<EntityManager> delegate;

    public EntityManagerProxy(TransactionContext context, EntityManagerFactory fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
        delegate = Value.lazy(() -> {
            TransactionStatus currentStatus = context.currentStatus();
            if (currentStatus != null) {
                return currentStatus.getEntityManager();
            }

            return fallbackFactory.createEntityManager();
        });
    }

    @Override
    public void persist(Object entity) {
        delegate.get().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return delegate.get().merge(entity);
    }

    @Override
    public void remove(Object entity) {
        delegate.get().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return delegate.get().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return delegate.get().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return delegate.get().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return delegate.get().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, FindOption... options) {
        return delegate.get().find(entityClass, primaryKey, options);
    }

    @Override
    public <T> T find(EntityGraph<T> entityGraph, Object primaryKey, FindOption... options) {
        return delegate.get().find(entityGraph, primaryKey, options);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return delegate.get().getReference(entityClass, primaryKey);
    }

    @Override
    public <T> T getReference(T entity) {
        return delegate.get().getReference(entity);
    }

    @Override
    public void flush() {
        delegate.get().flush();
    }

    @Override
    public FlushModeType getFlushMode() {
        return delegate.get().getFlushMode();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        delegate.get().setFlushMode(flushMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        delegate.get().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.get().lock(entity, lockMode, properties);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, LockOption... options) {
        delegate.get().lock(entity, lockMode, options);
    }

    @Override
    public void refresh(Object entity) {
        delegate.get().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        delegate.get().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        delegate.get().refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.get().refresh(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity, RefreshOption... options) {
        delegate.get().refresh(entity, options);
    }

    @Override
    public void clear() {
        delegate.get().clear();
    }

    @Override
    public void detach(Object entity) {
        delegate.get().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return delegate.get().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return delegate.get().getLockMode(entity);
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return delegate.get().getCacheRetrieveMode();
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        delegate.get().setCacheRetrieveMode(cacheRetrieveMode);
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return delegate.get().getCacheStoreMode();
    }

    @Override
    public void setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        delegate.get().setCacheStoreMode(cacheStoreMode);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        delegate.get().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.get().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return delegate.get().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return delegate.get().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> selectQuery) {
        return delegate.get().createQuery(selectQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate<?> updateQuery) {
        return delegate.get().createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete<?> deleteQuery) {
        return delegate.get().createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return delegate.get().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return delegate.get().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return delegate.get().createNamedQuery(name, resultClass);
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> reference) {
        return delegate.get().createQuery(reference);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return delegate.get().createNativeQuery(sqlString);
    }

    @Override
    public <T> Query createNativeQuery(String sqlString, Class<T> resultClass) {
        return delegate.get().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return delegate.get().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return delegate.get().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return delegate.get().createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class<?>... resultClasses) {
        return delegate.get().createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return delegate.get().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        delegate.get().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return delegate.get().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return delegate.get().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return delegate.get().getDelegate();
    }

    @Override
    public void close() {
        delegate.get().close();
        delegate.set(null);
    }

    @Override
    public boolean isOpen() {
        return delegate.get().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return delegate.get().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        if (delegate.isSet()) {
            return delegate.get().getEntityManagerFactory();
        } else {
            return fallbackFactory;
        }
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.get().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.get().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return delegate.get().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return delegate.get().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return delegate.get().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return delegate.get().getEntityGraphs(entityClass);
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> action) {
        delegate.get().runWithConnection(action);
    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> function) {
        return delegate.get().callWithConnection(function);
    }
}
