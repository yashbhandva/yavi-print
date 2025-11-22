package com.web_app.yaviPrint.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

@Slf4j
public class CorsUtils {

    /**
     * Check if the current request is a CORS request
     */
    public static boolean isCorsRequest() {
        HttpServletRequest request = getCurrentRequest();
        return request != null && request.getHeader("Origin") != null;
    }

    /**
     * Get the origin of the current request
     */
    public static String getRequestOrigin() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("Origin") : null;
    }

    /**
     * Log CORS headers for debugging
     */
    public static void logCorsHeaders() {
        if (log.isDebugEnabled()) {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                log.debug("CORS Headers for request from origin: {}", request.getHeader("Origin"));
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    if (headerName.toLowerCase().contains("origin") ||
                            headerName.toLowerCase().contains("access-control")) {
                        log.debug("{}: {}", headerName, request.getHeader(headerName));
                    }
                }
            }
        }
    }

    /**
     * Check if request is a preflight request
     */
    public static boolean isPreflightRequest() {
        HttpServletRequest request = getCurrentRequest();
        return request != null &&
                "OPTIONS".equalsIgnoreCase(request.getMethod()) &&
                request.getHeader("Access-Control-Request-Method") != null;
    }

    private static HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}