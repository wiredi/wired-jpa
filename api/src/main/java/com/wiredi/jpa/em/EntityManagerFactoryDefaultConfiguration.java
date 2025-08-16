package com.wiredi.jpa.em;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.StartupDiagnostics;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnBean;
import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import java.util.List;

@DefaultConfiguration
public class EntityManagerFactoryDefaultConfiguration {

    private static final Logging logger = Logging.getInstance(EntityManagerFactoryDefaultConfiguration.class);

    @Provider
    @ConditionalOnBean(type = EntityManagerVendor.class)
    public EntityManagerFactory entityManagerFactory(
            List<EntityManagerVendor> vendors,
            DataSource dataSource,
            List<EntityMetadataProvider<?>> entityMetadataProviders,
            StartupDiagnostics startupDiagnostics
    ) {
        if (vendors.size() > 1) {
            logger.warn(() -> "Multiple EntityManagerVendors have been found, only the first one will be used. Found: " + vendors);
        }

        EntityManagerVendor entityManagerVendor = vendors.getFirst();

        logger.info(() -> "Loading EntityManagerVendor: " + entityManagerVendor);
        return startupDiagnostics.measure("EntityManagerFactory.create", () -> entityManagerVendor.getEntityManager(new EntityManagerFactoryContext(dataSource, entityMetadataProviders)))
                .then((timedValue) -> logger.info(() -> "Loaded entity manager from Vendor " + entityManagerVendor.getClass().getSimpleName() + " in " + timedValue.time()))
                .value();
    }
}
