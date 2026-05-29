package com.quantacus.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Full payload returned by GET /api/v1/jobs/{jobId}/dashboard.
// Contains the job, all its products (with alerts + prices embedded),
// and computed summary counts for the top-level cards in the UI.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private JobResponse job;
    private List<ProductResponse> products;
    private Summary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {

        private int totalProducts;

        // Average quality score across all products (0.0 – 100.0)
        private double avgQualityScore;

        private long totalAlerts;
        private long errorCount;
        private long warningCount;

        // Number of products flagged as duplicates
        private long duplicateCount;

        // Products where seller's price is lower than ALL competitor prices
        private long priceBelowAllCompetitors;

        // Products where seller's price is higher than ALL competitor prices
        private long priceAboveAllCompetitors;
    }
}
