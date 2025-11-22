package com.web_app.yaviPrint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cors")
public class CorsTestController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testCors() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "CORS is working correctly");
        response.put("timestamp", System.currentTimeMillis());
        response.put("corsEnabled", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/headers")
    public ResponseEntity<Map<String, Object>> getCorsHeaders() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("corsHeaders", Map.of(
                "Access-Control-Allow-Origin", "Configured origins",
                "Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS",
                "Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, etc.",
                "Access-Control-Allow-Credentials", "true"
        ));

        return ResponseEntity.ok(response);
    }
}