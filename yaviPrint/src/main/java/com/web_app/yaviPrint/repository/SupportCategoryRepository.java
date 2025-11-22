package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.SupportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportCategoryRepository extends JpaRepository<SupportCategory, Long> {
    List<SupportCategory> findByDepartment(String department);
    List<SupportCategory> findByIsActiveTrue();
    Optional<SupportCategory> findByName(String name);
}