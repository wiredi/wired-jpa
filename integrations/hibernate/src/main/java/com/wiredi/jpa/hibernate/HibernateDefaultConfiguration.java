package com.wiredi.jpa.hibernate;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.jpa.em.EntityMetadataProvider;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;
import com.wiredi.runtime.time.Timed;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultConfiguration
public class HibernateDefaultConfiguration {

    private static final Logging logger = Logging.getInstance(HibernateDefaultConfiguration.class);

    @Provider
    public StandardServiceRegistry standardServiceRegistry(
            DataSource dataSource,
            HibernateProperties hibernateProperties
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(Environment.JAKARTA_NON_JTA_DATASOURCE, dataSource);
        if (hibernateProperties.dialect() != null) {
            props.put(Environment.DIALECT, hibernateProperties.dialect());
        }
        props.put(Environment.HBM2DDL_AUTO, hibernateProperties.ddlAuto().getExternalHbm2ddlName());
        props.put(Environment.SHOW_SQL, hibernateProperties.showSql());
        props.put(Environment.FORMAT_SQL, hibernateProperties.formatSql());
        props.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        props.put(Environment.JAKARTA_TRANSACTION_TYPE, "RESOURCE_LOCAL");
        props.put("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_HOLD");
        props.put("hibernate.connection.provider_disables_autocommit", "true");

        return new StandardServiceRegistryBuilder()
                .applySettings(props)
                .build();
    }
}
