package com.quantacus.dashboard.service;

import com.quantacus.dashboard.dto.response.JobResponse;
import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.enums.JobStatus;
import com.quantacus.dashboard.enums.JobType;
import com.quantacus.dashboard.exception.BusinessException;
import com.quantacus.dashboard.exception.ResourceNotFoundException;
import com.quantacus.dashboard.repository.JobRepository;
import com.quantacus.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Job create(boolean enhanceTitle, JobType jobType, String filePath) {
        Job job = Job.builder()
                .status(JobStatus.PENDING)
                .jobType(jobType)
                .enhanceTitle(enhanceTitle)
                .build();

        if (jobType == JobType.VIDEO_UPLOAD) {
            job.setVideoPath(filePath);
        } else {
            job.setCsvPath(filePath);
        }

        Job saved = jobRepository.save(job);
        log.info("Created job {} (type={}, enhanceTitle={})", saved.getId(), jobType, enhanceTitle);
        return saved;
    }

    @Transactional(readOnly = true)
    public Job getById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", id));
    }

    @Transactional(readOnly = true)
    public Page<Job> list(Pageable pageable) {
        return jobRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public Job updateStatus(UUID jobId, JobStatus newStatus) {
        Job job = getById(jobId);
        log.info("Job {} status transition: {} → {}", jobId, job.getStatus(), newStatus);

        job.setStatus(newStatus);

        // Record when active processing actually starts
        if (newStatus == JobStatus.EXTRACTING) {
            job.setStartedAt(LocalDateTime.now());
        }
        // Record when the job reaches a terminal state
        if (newStatus == JobStatus.COMPLETED || newStatus == JobStatus.FAILED) {
            job.setCompletedAt(LocalDateTime.now());
        }

        return jobRepository.save(job);
    }

    @Transactional
    public Job fail(UUID jobId, String errorMessage) {
        Job job = getById(jobId);
        job.setStatus(JobStatus.FAILED);
        job.setErrorMessage(errorMessage);
        job.setCompletedAt(LocalDateTime.now());
        log.error("Job {} failed: {}", jobId, errorMessage);
        return jobRepository.save(job);
    }

    @Transactional
    public void delete(UUID id) {
        Job job = getById(id);

        // Prevent deleting a job while the pipeline is running
        if (job.getStatus() == JobStatus.EXTRACTING
                || job.getStatus() == JobStatus.VALIDATING
                || job.getStatus() == JobStatus.ENHANCING_TITLE
                || job.getStatus() == JobStatus.FETCHING_PRICES) {
            throw new BusinessException(
                    "Cannot delete job " + id + " while it is actively processing (status: "
                    + job.getStatus() + ")");
        }

        jobRepository.delete(job);
        log.info("Deleted job {}", id);
    }

    // Converts a Job entity to a JobResponse DTO.
    // productCount is fetched with a COUNT query to avoid loading the full products list.
    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .status(job.getStatus())
                .jobType(job.getJobType())
                .enhanceTitle(job.isEnhanceTitle())
                .videoPath(job.getVideoPath())
                .csvPath(job.getCsvPath())
                .errorMessage(job.getErrorMessage())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .productCount(productRepository.countByJobId(job.getId()))
                .build();
    }
}
