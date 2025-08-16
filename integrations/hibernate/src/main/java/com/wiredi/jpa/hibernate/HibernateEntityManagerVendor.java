package com.wiredi.jpa.hibernate;

import com.wiredi.annotations.Wire;
import com.wiredi.jpa.em.EntityManagerFactoryContext;
import com.wiredi.jpa.em.EntityManagerVendor;
import com.wiredi.logging.Logging;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;

@Wire
public record HibernateEntityManagerVendor(
        StandardServiceRegistry registry
) implements EntityManagerVendor {

    private static final Logging logger = Logging.getInstance(HibernateEntityManagerVendor.class);

    @Override
    public EntityManagerFactory getEntityManager(EntityManagerFactoryContext context) {
        MetadataSources sources = new MetadataSources(registry);
        logger.info(() -> "Registering " + context.entityMetadataProviders().size() + " entity classes");
        logger.debug(() -> "Registering entities " + context.entityMetadataProviders());
        context.entityMetadataProviders().forEach(it -> sources.addAnnotatedClass(it.entityClass()));
        Metadata metadata = sources.buildMetadata();

        return metadata.buildSessionFactory()
                .unwrap(EntityManagerFactory.class);
    }
}
