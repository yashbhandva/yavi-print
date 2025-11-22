package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {
    List<UserLoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId);

    @Query("SELECT h FROM UserLoginHistory h WHERE h.user.id = :userId AND h.loginTime >= :startDate ORDER BY h.loginTime DESC")
    List<UserLoginHistory> findRecentLoginsByUserId(Long userId, LocalDateTime startDate);

    long countByUserIdAndSuccessTrue(Long userId);

    @Query("SELECT COUNT(DISTINCT h.user.id) FROM UserLoginHistory h WHERE h.loginTime >= :startDate")
    long countActiveUsersSince(LocalDateTime startDate);
}