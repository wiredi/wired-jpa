package com.wiredi.jpa.eclipse;

import com.wiredi.annotations.Wire;
import com.wiredi.jpa.em.EntityManagerFactoryContext;
import com.wiredi.jpa.em.EntityManagerVendor;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;

import java.util.List;
import java.util.Map;

@Wire
public class EclipseLinkEntityManagerVendor implements EntityManagerVendor {

    private final EclipseLinkProperties properties;

    public EclipseLinkEntityManagerVendor(EclipseLinkProperties properties) {
        this.properties = properties;
    }

    @Override
    public EntityManagerFactory getEntityManager(
            EntityManagerFactoryContext context
    ) {

        // 1) Liste deiner Entity-Klassen (vollqualifizierte Namen)
        List<String> entities = context.entityMetadataProviders().stream()
                .map(it -> it.entityClass().getName())
                .toList();

        // 2) Erstelle dein dynamisches PersistenceUnitInfo
        DynamicPersistenceUnitInfo puInfo = new DynamicPersistenceUnitInfo(
                "dynamic-unit",
                entities,
                Thread.currentThread().getContextClassLoader(),
                context.dataSource()
        );

        // 3) Properties für JDBC & EclipseLink
        Map<String, Object> props = properties.buildProperties();
//        props.put("jakarta.persistence.nonJtaDataSource", dataSource);

        // EclipseLink-spezifisch:
//        props.put("eclipselink.logging.level", "FINE");
        props.put("eclipselink.logging.logger", CustomSLF4JLogger.class.getName());

        // 4) Boot EclipseLink
        PersistenceProvider provider = new PersistenceProvider();
        return provider.createContainerEntityManagerFactory(puInfo, props);
    }
}
