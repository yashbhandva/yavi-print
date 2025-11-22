package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.DatabaseHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/database")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DatabaseHealthController {

    private final DatabaseHealthService databaseHealthService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        var health = databaseHealthService.checkDatabaseHealth();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/migrations")
    public ResponseEntity<?> getMigrationHistory() {
        var migrations = databaseHealthService.getMigrationHistory();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", migrations
        ));
    }

    @GetMapping("/tables")
    public ResponseEntity<?> getTableInfo() {
        // Implementation to get table information
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Table information endpoint"
        ));
    }
}