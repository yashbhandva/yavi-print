package com.web_app.yaviPrint.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayMigrationConfig {

    @Bean
    @Profile("dev")
    public FlywayMigrationStrategy devMigrationStrategy() {
        return flyway -> {
            // For development, we can be more lenient
            flyway.clean();
            flyway.migrate();
        };
    }

    @Bean
    @Profile("prod")
    public FlywayMigrationStrategy prodMigrationStrategy() {
        return flyway -> {
            // For production, only migrate, never clean
            flyway.repair(); // Repair if any issues
            flyway.migrate();
        };
    }

    @Bean
    @Profile("test")
    public FlywayMigrationStrategy testMigrationStrategy() {
        return flyway -> {
            // For tests, clean and migrate
            flyway.clean();
            flyway.migrate();
        };
    }
}