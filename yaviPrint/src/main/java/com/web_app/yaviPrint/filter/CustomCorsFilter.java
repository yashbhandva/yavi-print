package com.web_app.yaviPrint.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Get origin from request
        String origin = request.getHeader("Origin");

        // Check if origin is allowed
        if (isOriginAllowed(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Expose-Headers",
                "Authorization, Content-Disposition, X-Total-Count, X-API-Version");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("CORS preflight request handled for origin: {}", origin);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isOriginAllowed(String origin) {
        if (origin == null) return false;

        List<String> allowedOriginsList = Arrays.asList(allowedOrigins);

        // Check exact match
        if (allowedOriginsList.contains(origin)) {
            return true;
        }

        // Check pattern match (for subdomains)
        for (String allowedOrigin : allowedOriginsList) {
            if (allowedOrigin.contains("*") && origin.matches(allowedOrigin.replace("*", ".*"))) {
                return true;
            }
        }

        log.warn("CORS request blocked from origin: {}", origin);
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CustomCorsFilter initialized with allowed origins: {}", Arrays.toString(allowedOrigins));
    }

    @Override
    public void destroy() {
        log.info("CustomCorsFilter destroyed");
    }
}