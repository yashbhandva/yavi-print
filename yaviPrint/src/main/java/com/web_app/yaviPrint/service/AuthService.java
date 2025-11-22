package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.UserLoginDTO;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserLoginHistoryService userLoginHistoryService;

    public Map<String, Object> login(UserLoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email first");
        }

        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Record login success
        userLoginHistoryService.recordLoginSuccess(user, getClientIP(), getUserAgent());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("refreshToken", refreshToken);
        response.put("user", mapToUserResponse(user));

        return response;
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            User user = (User) userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String newToken = jwtService.generateToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("token", newToken);
                response.put("refreshToken", refreshToken); // Return same refresh token
                return response;
            } else {
                throw new RuntimeException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    private Map<String, Object> mapToUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("name", user.getName());
        userResponse.put("email", user.getEmail());
        userResponse.put("phone", user.getPhone());
        userResponse.put("role", user.getRole().name());
        userResponse.put("enabled", user.isEnabled());
        return userResponse;
    }

    // Helper methods to get client info
    private String getClientIP() {
        // Implementation to get client IP from request
        return "127.0.0.1"; // Placeholder
    }

    private String getUserAgent() {
        // Implementation to get user agent from request
        return "Unknown"; // Placeholder
    }
}