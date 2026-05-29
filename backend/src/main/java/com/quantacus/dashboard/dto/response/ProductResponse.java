package com.quantacus.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quantacus.dashboard.enums.ExtractionSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private UUID id;
    private UUID jobId;

    // Core product fields matching the CSV schema
    private String skuId;
    private String productTitle;
    private String enhancedTitle;
    private String description;
    private String brand;
    private String category;
    private BigDecimal price;
    private BigDecimal mrp;
    private String imageUrl;
    private String productUrl;
    private String availability;
    private String color;
    private String size;
    private String material;

    // Computed by ValidationService — range 0 to 100
    private Integer qualityScore;

    private ExtractionSource extractionSource;
    private boolean duplicate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Embedded collections — present only on dashboard / detail endpoints
    private List<AlertResponse> alerts;
    private List<CompetitorPriceResponse> competitorPrices;
}
