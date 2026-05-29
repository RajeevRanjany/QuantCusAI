package com.quantacus.dashboard.repository;

import com.quantacus.dashboard.entity.ValidationAlert;
import com.quantacus.dashboard.enums.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ValidationAlertRepository extends JpaRepository<ValidationAlert, UUID> {

    List<ValidationAlert> findByProductId(UUID productId);

    // All alerts for a job, ordered by severity so errors appear first
    @Query("SELECT va FROM ValidationAlert va WHERE va.job.id = :jobId " +
           "ORDER BY CASE va.severity WHEN 'ERROR' THEN 0 WHEN 'WARNING' THEN 1 ELSE 2 END")
    List<ValidationAlert> findByJobIdOrderBySeverity(@Param("jobId") UUID jobId);

    long countByJobId(UUID jobId);

    long countByJobIdAndSeverity(UUID jobId, AlertSeverity severity);

    // Bulk delete — used when re-validating a product after a manual edit.
    // @Modifying + JPQL issues a single DELETE statement rather than
    // loading all alert entities and removing them one by one.
    @Modifying
    @Query("DELETE FROM ValidationAlert va WHERE va.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}
