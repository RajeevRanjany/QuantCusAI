package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.dto.response.JobResponse;
import com.quantacus.dashboard.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs")
public class JobController {

    private final JobService jobService;

    @Operation(summary = "List all jobs, newest first")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> list(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<JobResponse> page = jobService.list(pageable).map(jobService::toResponse);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @Operation(summary = "Get a job by ID")
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> get(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.toResponse(jobService.getById(jobId))));
    }
}
