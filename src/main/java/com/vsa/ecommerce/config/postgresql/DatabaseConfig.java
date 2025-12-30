package com.vsa.ecommerce.config.postgresql;

import com.vsa.ecommerce.common.repository.BaseRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Advanced Database Configuration.
 * Manually configures DataSource (HikariCP) and EntityManagerFactory for better
 * control.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.vsa.ecommerce.domain.repository", repositoryBaseClass = BaseRepositoryImpl.class)
@RequiredArgsConstructor
public class DatabaseConfig {

    private final DatabaseProperties databaseProperties;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(databaseProperties.getUrl());
        hikariConfig.setUsername(databaseProperties.getUsername());
        hikariConfig.setPassword(databaseProperties.getPassword());
        hikariConfig.setDriverClassName(databaseProperties.getDriverClassName());

        // HikariCP Custom Parameters
        DatabaseProperties.Hikari hikari = databaseProperties.getHikari();
        hikariConfig.setMaximumPoolSize(hikari.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(hikari.getMinimumIdle());
        hikariConfig.setIdleTimeout(hikari.getIdleTimeout());
        hikariConfig.setConnectionTimeout(hikari.getConnectionTimeout());
        hikariConfig.setMaxLifetime(hikari.getMaxLifetime());
        hikariConfig.setPoolName(hikari.getPoolName());

        // Performance Tuning
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.vsa.ecommerce.domain.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();
        // Hibernate Settings
        DatabaseProperties.Jpa jpa = databaseProperties.getJpa();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put("hibernate.show_sql", String.valueOf(jpa.isShowSql()));
        jpaProperties.put("hibernate.format_sql", String.valueOf(jpa.isFormatSql()));
        jpaProperties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        jpaProperties.put("hibernate.hbm2ddl.auto", jpa.getDdlAuto());

        // Performance & Batching
        jpaProperties.put("hibernate.jdbc.batch_size", "20");
        jpaProperties.put("hibernate.order_inserts", "true");
        jpaProperties.put("hibernate.order_updates", "true");
        jpaProperties.put("hibernate.jdbc.fetch_size", "50");

        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
