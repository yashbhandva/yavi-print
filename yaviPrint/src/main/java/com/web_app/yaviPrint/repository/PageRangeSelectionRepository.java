package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PageRangeSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRangeSelectionRepository extends JpaRepository<PageRangeSelection, Long> {
    List<PageRangeSelection> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}