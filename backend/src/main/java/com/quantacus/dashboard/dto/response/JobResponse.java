package com.quantacus.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quantacus.dashboard.enums.JobStatus;
import com.quantacus.dashboard.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {

    private UUID id;
    private JobStatus status;
    private JobType jobType;
    private boolean enhanceTitle;

    // Null when job type is CSV_UPLOAD or MANUAL
    private String videoPath;

    // Null when job type is VIDEO_UPLOAD
    private String csvPath;

    // Only present when status = FAILED
    private String errorMessage;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Total number of products created under this job
    private long productCount;
}
