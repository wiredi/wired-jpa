package com.wiredi.jpa;

import com.google.auto.service.AutoService;
import com.wiredi.runtime.WireContainer;
import com.wiredi.runtime.domain.WireContainerCallback;
import com.wiredi.runtime.domain.provider.IdentifiableProvider;
import com.wiredi.runtime.domain.provider.TypeIdentifier;
import com.wiredi.runtime.properties.Key;
import com.wiredi.tests.callback.TestCallback;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;

@AutoService(WireContainerCallback.class)
public class PostgresContainerCallback implements WireContainerCallback, TestCallback {

    private static final TypeIdentifier<PostgreSQLContainer<?>> testContainerType = TypeIdentifier.of(PostgreSQLContainer.class).withGeneric(PostgreSQLContainer.class);

    @Override
    public void loadingStarted(@NotNull WireContainer wireContainer) {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.4");
        postgres.start();
        wireContainer.announce(
                IdentifiableProvider.builder(testContainerType)
                        .withAdditionalType(TypeIdentifier.of(JdbcDatabaseContainer.class).withGeneric(PostgreSQLContainer.class))
                        .withInstance(postgres)
        );
        wireContainer.environment().properties().add(Key.just("hikari.jdbc-url"), postgres.getJdbcUrl());
        wireContainer.environment().properties().add(Key.just("hikari.username"), postgres.getUsername());
        wireContainer.environment().properties().add(Key.just("hikari.password"), postgres.getPassword());
        wireContainer.environment().properties().add(Key.just("hikari.driver-class"), postgres.getDriverClassName());
    }

    @Override
    public void destroyed(@NotNull WireContainer wireContainer) {
        wireContainer.tryGet(testContainerType).ifPresent(JdbcDatabaseContainer::stop);
    }

    @Override
    public void afterEach(ExtensionContext context, WireContainer wireContainer) throws Exception {
        wireContainer.tryGet(testContainerType).ifPresent(container -> {
            try {
                Connection connection = container.createConnection("");
                connection.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");
                connection.createStatement().execute("SELECT 'truncate table '||\n" +
                        "          string_agg(format('%I.%I', table_schema, table_name), ',')||\n" +
                        "          ' cascade restart identity;'\n" +
                        "FROM information_schema.tables\n" +
                        "WHERE table_schema='public'\n" +
                        "  AND table_type='BASE TABLE';");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
