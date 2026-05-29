package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.response.AlertResponse;
import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts")
public class AlertController {

    private final AlertService alertService;

    @Operation(summary = "Get all alerts for a job, errors first")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> list(@RequestParam UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getByJob(jobId)));
    }

    @Operation(summary = "Mark an alert as resolved")
    @PostMapping("/{alertId}/resolve")
    public ResponseEntity<ApiResponse<AlertResponse>> resolve(@PathVariable UUID alertId) {
        return ResponseEntity.ok(ApiResponse.success(alertService.resolve(alertId)));
    }
}
