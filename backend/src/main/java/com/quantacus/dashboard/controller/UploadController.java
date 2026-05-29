package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.dto.response.JobResponse;
import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.enums.JobType;
import com.quantacus.dashboard.service.CsvService;
import com.quantacus.dashboard.service.FileStorageService;
import com.quantacus.dashboard.service.JobService;
import com.quantacus.dashboard.service.PipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Upload")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final JobService jobService;
    private final PipelineService pipelineService;
    private final CsvService csvService;

    @Operation(summary = "Upload a product video to start a processing job")
    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<JobResponse>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "enhanceTitle", defaultValue = "false") boolean enhanceTitle) {

        String path = fileStorageService.save(file);
        Job job = jobService.create(enhanceTitle, JobType.VIDEO_UPLOAD, path);
        pipelineService.runForVideoUpload(job.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobService.toResponse(job)));
    }

    @Operation(summary = "Upload a product CSV to start a processing job")
    @PostMapping(value = "/upload-products-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<JobResponse>> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "enhanceTitle", defaultValue = "false") boolean enhanceTitle) {

        String path = fileStorageService.save(file);
        Job job = jobService.create(enhanceTitle, JobType.CSV_UPLOAD, path);
        csvService.parseAndSave(file, job);
        pipelineService.resumeAfterFallback(job.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobService.toResponse(job)));
    }
}
