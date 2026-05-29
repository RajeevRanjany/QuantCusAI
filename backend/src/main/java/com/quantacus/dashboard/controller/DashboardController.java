package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.response.AlertResponse;
import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.dto.response.DashboardResponse;
import com.quantacus.dashboard.dto.response.ProductResponse;
import com.quantacus.dashboard.enums.AlertSeverity;
import com.quantacus.dashboard.service.AlertService;
import com.quantacus.dashboard.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final ProductService productService;
    private final AlertService alertService;

    @Operation(summary = "Get quality summary for a job")
    @GetMapping("/quality-summary")
    public ResponseEntity<ApiResponse<DashboardResponse.Summary>> qualitySummary(
            @RequestParam UUID jobId) {

        List<ProductResponse> products = productService.getByJob(jobId);
        List<AlertResponse> alerts = alertService.getByJob(jobId);

        double avgScore = products.stream()
                .filter(p -> p.getQualityScore() != null)
                .mapToInt(ProductResponse::getQualityScore)
                .average()
                .orElse(0.0);

        long errorCount = alerts.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.ERROR).count();

        long warningCount = alerts.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.WARNING).count();

        long duplicateCount = products.stream()
                .filter(ProductResponse::isDuplicate).count();

        long priceBelowAll = products.stream()
                .filter(p -> p.getPrice() != null
                        && !p.getCompetitorPrices().isEmpty()
                        && p.getCompetitorPrices().stream()
                                .allMatch(c -> c.getPriceDelta() != null
                                        && c.getPriceDelta().signum() > 0))
                .count();

        long priceAboveAll = products.stream()
                .filter(p -> p.getPrice() != null
                        && !p.getCompetitorPrices().isEmpty()
                        && p.getCompetitorPrices().stream()
                                .allMatch(c -> c.getPriceDelta() != null
                                        && c.getPriceDelta().signum() < 0))
                .count();

        DashboardResponse.Summary summary = DashboardResponse.Summary.builder()
                .totalProducts(products.size())
                .avgQualityScore(avgScore)
                .totalAlerts(alerts.size())
                .errorCount(errorCount)
                .warningCount(warningCount)
                .duplicateCount(duplicateCount)
                .priceBelowAllCompetitors(priceBelowAll)
                .priceAboveAllCompetitors(priceAboveAll)
                .build();

        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
