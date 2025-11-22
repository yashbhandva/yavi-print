package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.util.CorsUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/cors")
@RequiredArgsConstructor
public class CorsDebugController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCorsInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Request info
        response.put("origin", request.getHeader("Origin"));
        response.put("method", request.getMethod());
        response.put("isCorsRequest", CorsUtils.isCorsRequest());
        response.put("isPreflightRequest", CorsUtils.isPreflightRequest());

        // CORS-specific headers
        Map<String, String> corsHeaders = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.toLowerCase().contains("origin") ||
                    headerName.toLowerCase().contains("access-control")) {
                corsHeaders.put(headerName, request.getHeader(headerName));
            }
        }
        response.put("corsHeaders", corsHeaders);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-post")
    public ResponseEntity<Map<String, Object>> testCorsPost(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "CORS POST request successful");
        response.put("receivedData", body);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/test-put/{id}")
    public ResponseEntity<Map<String, Object>> testCorsPut(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "CORS PUT request successful");
        response.put("id", id);
        response.put("receivedData", body);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/test-delete/{id}")
    public ResponseEntity<Map<String, Object>> testCorsDelete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "CORS DELETE request successful");
        response.put("deletedId", id);

        return ResponseEntity.ok(response);
    }
}