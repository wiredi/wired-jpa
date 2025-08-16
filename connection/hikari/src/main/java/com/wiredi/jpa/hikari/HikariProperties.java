package com.wiredi.jpa.hikari;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.Nullable;

import java.sql.Driver;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.Enumeration;

@PropertyBinding(prefix = "hikari")
public record HikariProperties(
        String jdbcUrl,
        String username,
        String password,

        @Property(defaultValue = "10")
        int maximumPoolSize,
        @Property(defaultValue = "PT2M")
        Duration keepaliveTime,
        @Property(defaultValue = "false")
        boolean allowPoolSuspension,
        @Property(defaultValue = "false")
        boolean isReadOnly,
        @Property(defaultValue = "false")
        boolean isIsolateInternalQueries,
        @Property(defaultValue = "false")
        boolean isRegisterMbeans,
        @Property(defaultValue = "false")
        boolean isAllowPoolSuspension,
        @Nullable
        String poolName,
        @Nullable
        String connectionInitSql,
        @Nullable
        String connectionTestQuery,
        @Nullable
        String dataSourceClassName,
        @Nullable
        String dataSourceJndiName,
        @Nullable
        String driverClassName,
        @Nullable
        String schema,
        @Nullable
        String transactionIsolationName
) {
    public HikariConfig createConfig() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setKeepaliveTime(keepaliveTime.toMillis());
        hikariConfig.setAllowPoolSuspension(allowPoolSuspension);
        hikariConfig.setReadOnly(isReadOnly);
        hikariConfig.setIsolateInternalQueries(isIsolateInternalQueries);
        hikariConfig.setRegisterMbeans(isRegisterMbeans);
        hikariConfig.setAllowPoolSuspension(isAllowPoolSuspension);

        String determinedDriverClassName = driverClassName();
        if(determinedDriverClassName != null) hikariConfig.setDriverClassName(determinedDriverClassName);

        if(poolName != null) hikariConfig.setPoolName(poolName);
        if(connectionInitSql != null) hikariConfig.setConnectionInitSql(connectionInitSql);
        if(connectionTestQuery != null) hikariConfig.setConnectionTestQuery(connectionTestQuery);
        if(dataSourceClassName != null) hikariConfig.setDataSourceClassName(dataSourceClassName);
        if(dataSourceJndiName != null) hikariConfig.setDataSourceJNDI(dataSourceJndiName);
        if(schema != null) hikariConfig.setConnectionTestQuery(schema);
        if(transactionIsolationName != null) hikariConfig.setConnectionTestQuery(transactionIsolationName);

        return hikariConfig;
    }

    @Nullable
    @Override
    public String driverClassName() {
        if(driverClassName != null) {
            return driverClassName;
        } else {
            Driver driver = autoDetermineDriver();
            if (driver != null) {
                return driver.getClass().getName();
            }
        }

        return null;
    }

    @Nullable
    public static Driver autoDetermineDriver() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver driver = null;

        while (drivers.hasMoreElements()) {
            if (driver != null) {
                return null;
            }
            driver = drivers.nextElement();
        }

        return driver;
    }
}
