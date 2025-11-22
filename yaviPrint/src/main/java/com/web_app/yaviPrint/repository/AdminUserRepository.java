package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByEmail(String email);
    List<AdminUser> findByRole(String role);
    List<AdminUser> findByIsActiveTrue();

    @Query("SELECT a FROM AdminUser a WHERE a.lastLogin IS NOT NULL ORDER BY a.lastLogin DESC")
    List<AdminUser> findRecentlyActiveAdmins();

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}