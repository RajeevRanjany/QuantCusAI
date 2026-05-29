package com.quantacus.dashboard.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// Sent by the frontend when the user manually enters product data
// after video extraction is incomplete.
@Data
public class ManualProductRequest {

    @NotEmpty(message = "At least one product must be provided")
    @Valid
    private List<ProductEntry> products;

    @Data
    public static class ProductEntry {
        private String skuId;
        private String productTitle;
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
    }
}
