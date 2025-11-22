package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // UserRepository methods that work with your entity:
    Optional<User> findByEmail(String email); // email field exists
    Optional<User> findByVerificationToken(String token);  // verificationToken field exists
    boolean existsByEmail(String email); // email field exists
    List<User> findByRole(UserRole role);  // role field exists
    List<User> findByIsEnabledTrue();  // enabled field exists
    Optional<User> findByPhone(String phone);  // phone field exists
}