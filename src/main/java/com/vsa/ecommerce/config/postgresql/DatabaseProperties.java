package com.vsa.ecommerce.config.postgresql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for database configuration.
 * Maps values with prefix 'spring.datasource' and custom Hikari settings.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    private Hikari hikari = new Hikari();
    private Jpa jpa = new Jpa();

    @Getter
    @Setter
    public static class Hikari {
        private int maximumPoolSize = 10;
        private int minimumIdle = 5;
        private long idleTimeout = 300000;
        private long connectionTimeout = 20000;
        private long maxLifetime = 1200000;
        private String poolName = "VSAPostgresHikariPool";
    }

    @Getter
    @Setter
    public static class Jpa {
        private String ddlAuto = "update";
        private boolean showSql = false;
        private boolean formatSql = true;
    }
}
