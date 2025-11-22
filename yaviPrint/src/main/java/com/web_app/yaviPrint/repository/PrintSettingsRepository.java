package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PrintSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintSettingsRepository extends JpaRepository<PrintSettings, Long> {
}