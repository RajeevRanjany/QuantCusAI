package com.quantacus.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompetitorPriceResponse {

    private UUID id;
    private String platform;
    private String competitorTitle;
    private BigDecimal price;
    private String currency;
    private String url;
    private boolean simulated;
    private LocalDateTime fetchedAt;

    // Price difference relative to seller's price:
    // positive = competitor is more expensive, negative = competitor is cheaper
    private BigDecimal priceDelta;
}
