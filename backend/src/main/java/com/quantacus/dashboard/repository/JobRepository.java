package com.quantacus.dashboard.repository;

import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    // Used for the job list page — sorted newest first
    Page<Job> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Fetches the job together with its products in one query — used by the
    // dashboard endpoint where product count and product data are both needed
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.products WHERE j.id = :id")
    Optional<Job> findWithProductsById(@Param("id") UUID id);

    List<Job> findByStatus(JobStatus status);
}
