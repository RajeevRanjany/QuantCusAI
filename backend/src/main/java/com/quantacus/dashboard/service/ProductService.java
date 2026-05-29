package com.quantacus.dashboard.service;

import com.quantacus.dashboard.dto.request.ManualProductRequest;
import com.quantacus.dashboard.dto.request.UpdateProductRequest;
import com.quantacus.dashboard.dto.response.AlertResponse;
import com.quantacus.dashboard.dto.response.CompetitorPriceResponse;
import com.quantacus.dashboard.dto.response.ProductResponse;
import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.enums.ExtractionSource;
import com.quantacus.dashboard.exception.ResourceNotFoundException;
import com.quantacus.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ValidationService validationService;
    private final TitleEnhancementService titleEnhancementService;

    // Uses @EntityGraph — loads products with their alerts and competitor prices
    // in batch SELECTs to avoid N+1 queries on the dashboard endpoint
    @Transactional(readOnly = true)
    public List<ProductResponse> getByJob(UUID jobId) {
        return productRepository.findWithDetailsByJobId(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Used by the single-product detail and refresh-prices endpoints
    @Transactional(readOnly = true)
    public ProductResponse getWithDetails(UUID id) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toResponse(product);
    }

    // Applies only the non-null fields from the request (partial update).
    // Recomputes the quality score after each edit so the UI always shows fresh data.
    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (req.getSkuId()        != null) product.setSkuId(req.getSkuId());
        if (req.getProductTitle() != null) product.setProductTitle(req.getProductTitle());
        if (req.getDescription()  != null) product.setDescription(req.getDescription());
        if (req.getBrand()        != null) product.setBrand(req.getBrand());
        if (req.getCategory()     != null) product.setCategory(req.getCategory());
        if (req.getPrice()        != null) product.setPrice(req.getPrice());
        if (req.getMrp()          != null) product.setMrp(req.getMrp());
        if (req.getImageUrl()     != null) product.setImageUrl(req.getImageUrl());
        if (req.getProductUrl()   != null) product.setProductUrl(req.getProductUrl());
        if (req.getAvailability() != null) product.setAvailability(req.getAvailability());
        if (req.getColor()        != null) product.setColor(req.getColor());
        if (req.getSize()         != null) product.setSize(req.getSize());
        if (req.getMaterial()     != null) product.setMaterial(req.getMaterial());

        product.setQualityScore(validationService.computeQualityScore(product));
        return toResponse(productRepository.save(product));
    }

    // Converts the manual-entry request body into persisted Product entities.
    // Called by the fallback controller when the user types product data by hand.
    @Transactional
    public ProductResponse enhanceTitle(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        product.setEnhancedTitle(titleEnhancementService.enhance(product));
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public List<Product> createFromManual(Job job, ManualProductRequest request) {
        List<Product> products = request.getProducts().stream()
                .map(entry -> Product.builder()
                        .job(job)
                        .skuId(entry.getSkuId())
                        .productTitle(entry.getProductTitle())
                        .description(entry.getDescription())
                        .brand(entry.getBrand())
                        .category(entry.getCategory())
                        .price(entry.getPrice())
                        .mrp(entry.getMrp())
                        .imageUrl(entry.getImageUrl())
                        .productUrl(entry.getProductUrl())
                        .availability(entry.getAvailability())
                        .color(entry.getColor())
                        .size(entry.getSize())
                        .material(entry.getMaterial())
                        .extractionSource(ExtractionSource.MANUAL)
                        .build())
                .toList();

        List<Product> saved = productRepository.saveAll(products);
        log.info("Saved {} manual product(s) for job {}", saved.size(), job.getId());
        return saved;
    }

    // -------------------------------------------------------------------------
    // DTO mapping
    // -------------------------------------------------------------------------

    // toResponse expects that the product's alerts and competitorPrices collections
    // are already initialised (either loaded via @EntityGraph or empty by default).
    // Never call this outside a @Transactional method when lazy collections are needed.
    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .jobId(p.getJob().getId())
                .skuId(p.getSkuId())
                .productTitle(p.getProductTitle())
                .enhancedTitle(p.getEnhancedTitle())
                .description(p.getDescription())
                .brand(p.getBrand())
                .category(p.getCategory())
                .price(p.getPrice())
                .mrp(p.getMrp())
                .imageUrl(p.getImageUrl())
                .productUrl(p.getProductUrl())
                .availability(p.getAvailability())
                .color(p.getColor())
                .size(p.getSize())
                .material(p.getMaterial())
                .qualityScore(p.getQualityScore())
                .extractionSource(p.getExtractionSource())
                .duplicate(p.isDuplicate())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .alerts(p.getAlerts().stream()
                        .map(a -> AlertResponse.builder()
                                .id(a.getId())
                                .productId(p.getId())
                                .jobId(a.getJob().getId())
                                .alertType(a.getAlertType())
                                .severity(a.getSeverity())
                                .fieldName(a.getFieldName())
                                .message(a.getMessage())
                                .resolved(a.isResolved())
                                .createdAt(a.getCreatedAt())
                                .build())
                        .toList())
                .competitorPrices(p.getCompetitorPrices().stream()
                        .map(c -> {
                            BigDecimal delta = (p.getPrice() != null)
                                    ? c.getPrice().subtract(p.getPrice())
                                    : null;
                            return CompetitorPriceResponse.builder()
                                    .id(c.getId())
                                    .platform(c.getPlatform())
                                    .competitorTitle(c.getCompetitorTitle())
                                    .price(c.getPrice())
                                    .currency(c.getCurrency())
                                    .url(c.getUrl())
                                    .simulated(c.isSimulated())
                                    .fetchedAt(c.getFetchedAt())
                                    .priceDelta(delta)
                                    .build();
                        })
                        .toList())
                .build();
    }
}
