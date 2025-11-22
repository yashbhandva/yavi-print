package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.entity.UserLoginHistory;
import com.web_app.yaviPrint.repository.UserLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserLoginHistoryService {

    private final UserLoginHistoryRepository userLoginHistoryRepository;

    public void recordLoginSuccess(User user, String ipAddress, String userAgent) {
        UserLoginHistory history = new UserLoginHistory();
        history.setUser(user);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setUserAgent(userAgent);
        history.setDeviceType(determineDeviceType(userAgent));
        history.setSuccess(true);

        userLoginHistoryRepository.save(history);
    }

    public void recordLoginFailure(String email, String ipAddress, String userAgent, String failureReason) {
        UserLoginHistory history = new UserLoginHistory();
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setUserAgent(userAgent);
        history.setDeviceType(determineDeviceType(userAgent));
        history.setSuccess(false);
        history.setFailureReason(failureReason);

        userLoginHistoryRepository.save(history);
    }

    private String determineDeviceType(String userAgent) {
        if (userAgent.toLowerCase().contains("mobile")) {
            return "MOBILE";
        } else if (userAgent.toLowerCase().contains("tablet")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }
}