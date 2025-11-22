package com.web_app.yaviPrint.config;

import com.web_app.yaviPrint.security.JwtAuthenticationFilter;
import com.web_app.yaviPrint.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final com.web_app.yaviPrint.config.JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/api/cors/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // User endpoints
                        .requestMatchers("/api/users/profile**").authenticated()
                        .requestMatchers("/api/users/**").hasAnyRole("CUSTOMER", "SHOP_OWNER", "ADMIN")

                        // Shop endpoints (shop owners)
                        .requestMatchers("/api/shops/my-shop").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops", "POST").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/timings**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/services**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/pricing**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/verification**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/devices**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/status**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/analytics**").hasRole("SHOP_OWNER")
                        .requestMatchers("/api/shops/*/queue**").hasRole("SHOP_OWNER")

                        // Print requests
                        .requestMatchers("/api/print-requests", "POST").hasRole("CUSTOMER")
                        .requestMatchers("/api/print-requests/my-orders").hasRole("CUSTOMER")
                        .requestMatchers("/api/print-requests/shop/*").hasRole("SHOP_OWNER")

                        // File upload
                        .requestMatchers("/api/files/upload").authenticated()

                        // Payment
                        .requestMatchers("/api/payments/**").authenticated()
                        .requestMatchers("/api/wallet/**").authenticated()
                        .requestMatchers("/api/refunds/**").authenticated()

                        // Reviews
                        .requestMatchers("/api/reviews", "POST").hasRole("CUSTOMER")
                        .requestMatchers("/api/reviews/*/replies", "POST").hasRole("SHOP_OWNER")

                        // Notifications
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/notification-preferences/**").authenticated()

                        // Support
                        .requestMatchers("/api/support/**").authenticated()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/api/admin/database/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}