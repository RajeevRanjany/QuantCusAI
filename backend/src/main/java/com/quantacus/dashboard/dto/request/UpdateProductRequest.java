package com.quantacus.dashboard.dto.request;

import lombok.Data;

import java.math.BigDecimal;

// All fields are optional — only non-null values will overwrite the existing product.
// This allows partial updates from the manual-edit form in the UI.
@Data
public class UpdateProductRequest {

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
