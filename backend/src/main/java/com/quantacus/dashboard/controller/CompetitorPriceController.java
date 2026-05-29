package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.dto.response.CompetitorPriceResponse;
import com.quantacus.dashboard.entity.CompetitorPrice;
import com.quantacus.dashboard.service.CompetitorPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/competitor-prices")
@RequiredArgsConstructor
@Tag(name = "Competitor Prices")
public class CompetitorPriceController {

    private final CompetitorPriceService competitorPriceService;

    @Operation(summary = "Refresh simulated competitor prices for a product")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<List<CompetitorPriceResponse>>> refresh(
            @RequestParam UUID productId) {

        List<CompetitorPriceResponse> prices = competitorPriceService.refresh(productId)
                .stream()
                .map(c -> CompetitorPriceResponse.builder()
                        .id(c.getId())
                        .platform(c.getPlatform())
                        .competitorTitle(c.getCompetitorTitle())
                        .price(c.getPrice())
                        .currency(c.getCurrency())
                        .url(c.getUrl())
                        .simulated(c.isSimulated())
                        .fetchedAt(c.getFetchedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(prices));
    }
}
