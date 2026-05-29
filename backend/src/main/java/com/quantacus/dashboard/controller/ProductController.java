package com.quantacus.dashboard.controller;

import com.quantacus.dashboard.dto.request.UpdateProductRequest;
import com.quantacus.dashboard.dto.response.ApiResponse;
import com.quantacus.dashboard.dto.response.ProductResponse;
import com.quantacus.dashboard.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products for a job")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> list(@RequestParam UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByJob(jobId)));
    }

    @Operation(summary = "Get a product by ID")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getWithDetails(productId)));
    }

    @Operation(summary = "Partially update product fields")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request) {

        return ResponseEntity.ok(ApiResponse.success(productService.update(productId, request)));
    }

    @Operation(summary = "Generate and save an enhanced product title")
    @PostMapping("/{productId}/enhance-title")
    public ResponseEntity<ApiResponse<ProductResponse>> enhanceTitle(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.enhanceTitle(productId)));
    }
}
