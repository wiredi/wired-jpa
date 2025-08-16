package com.wiredi.jpa.hikari;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.Wire;
import com.wiredi.annotations.stereotypes.AutoConfiguration;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@AutoConfiguration
public class HikariAutoConfiguration {

    @Provider
    @ConditionalOnMissingBean(type = DataSource.class)
    public DataSource dataSource(HikariProperties properties) {
        HikariConfig config = properties.createConfig();

        config.setMaximumPoolSize(properties.maximumPoolSize());
        config.setAllowPoolSuspension(properties.allowPoolSuspension());
        config.setPoolName(properties.poolName());

        return new HikariDataSource(config);
    }
}
