package com.web_app.yaviPrint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseHealthService {

    private final DataSource dataSource;
    private final Flyway flyway;
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> checkDatabaseHealth() {
        try {
            // Check database connection
            jdbcTemplate.execute("SELECT 1");

            // Get migration info
            var migrationInfo = flyway.info();
            var appliedMigrations = migrationInfo.applied();
            var pendingMigrations = migrationInfo.pending();

            // Get table counts
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer shopCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shops", Integer.class);
            Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);

            return Map.of(
                    "status", "HEALTHY",
                    "database", "Connected",
                    "migrations", Map.of(
                            "applied", appliedMigrations.length,
                            "pending", pendingMigrations.length,
                            "current", appliedMigrations.length > 0 ?
                                    appliedMigrations[appliedMigrations.length - 1].getVersion().toString() : "None"
                    ),
                    "records", Map.of(
                            "users", userCount != null ? userCount : 0,
                            "shops", shopCount != null ? shopCount : 0,
                            "orders", orderCount != null ? orderCount : 0
                    ),
                    "timestamp", System.currentTimeMillis()
            );

        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Map.of(
                    "status", "UNHEALTHY",
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            );
        }
    }

    public List<Map<String, Object>> getMigrationHistory() {
        return jdbcTemplate.queryForList("""
            SELECT 
                installed_rank,
                version,
                description,
                type,
                script,
                installed_by,
                installed_on,
                execution_time,
                success
            FROM flyway_schema_history 
            ORDER BY installed_rank DESC
            LIMIT 10
        """);
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void scheduledHealthCheck() {
        var health = checkDatabaseHealth();
        if (!"HEALTHY".equals(health.get("status"))) {
            log.warn("Database health check failed: {}", health);
        }
    }
}