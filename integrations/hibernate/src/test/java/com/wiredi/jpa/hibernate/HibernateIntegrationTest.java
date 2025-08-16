package com.wiredi.jpa.hibernate;

import com.wiredi.jpa.TransactionAwareJpaRepository;
import com.wiredi.jpa.tx.TransactionContext;
import com.wiredi.jpa.tx.exception.TransactionRollbackException;
import com.wiredi.runtime.WireContainer;
import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;
import com.wiredi.runtime.properties.Key;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertSame;

class HibernateIntegrationTest {

    private TransactionContext transactionTemplate;
    private TransactionAwareJpaRepository<TestEntity, String> repository;

    @BeforeEach
    void setUpAll() {
        HashMap<Key, String> properties = new HashMap<>();
        properties.put(Key.format("hikari.jdbcUrl"), "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        properties.put(Key.format("hikari.username"), "sa");
        properties.put(Key.format("hikari.password"), "");
        properties.put(Key.format("hikari.driverClass"), "org.h2.Driver");

        properties.put(Key.format("hibernate.ddlAuto"), "create-drop");
//        properties.put(Key.format("hibernate.dialect"), "org.hibernate.dialect.H2Dialect");

        WiredApplicationInstance applicationInstance = WiredApplication.start(wireRepository -> {
            wireRepository.environment().setProperties(properties);
        });
        WireContainer repository = applicationInstance.wireContainer();
        transactionTemplate = repository.get(TransactionContext.class);
        this.repository = new TransactionAwareJpaRepository<>(TestEntity.class, repository.get(TransactionContext.class));
    }

    @Test
    void testExecute_withRequiresNewPropagation() {
        // Arrange
        TestEntity entity = new TestEntity();

        // Act
        transactionTemplate.run(status -> {
            EntityManager em = status.getEntityManager();
            em.persist(entity);
        });

        // Assert
        assertThat(repository.find(entity.getId()))
                .contains(entity);
    }

    @Test
    void rollingBackATransactionWorks() {
        // Arrange
        TestEntity entity = new TestEntity();
        RuntimeException testException = new RuntimeException("Test exception");
        entity.setName("Arrange");
        repository.save(entity);

        // Act
        try {
            transactionTemplate.run(status -> {
                EntityManager em = status.getEntityManager();
                em.find(TestEntity.class, entity.getId()).setName("Act");

                throw testException;
            });

            fail("Exception should have been thrown");
        } catch (TransactionRollbackException r) {
            assertSame(testException, r.getCause());
        } catch (Throwable throwable) {
            fail("Unexpected exception was thrown: " + throwable.getMessage());
        }

        // Assert
        assertThat(repository.find(entity.getId()))
                .map(TestEntity::getName)
                .contains("Arrange");
    }

    @Test
    void changesInACommittedTransactionAreFlushedToTheNextOne() {
        // Arrange
        TestEntity entity = new TestEntity();
        entity.setName("Arrange");
        repository.save(entity);

        // Act
        try {
            transactionTemplate.run(status -> {
                EntityManager em = status.getEntityManager();
                em.find(TestEntity.class, entity.getId()).setName("Act");
            });
        } catch (Throwable throwable) {
            fail("Unexpected exception was thrown: " + throwable.getMessage());
        }

        // Assert
        assertThat(repository.find(entity.getId()))
                .map(TestEntity::getName)
                .contains("Act");
    }
}
