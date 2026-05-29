package com.quantacus.dashboard.service;

import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.entity.ValidationAlert;
import com.quantacus.dashboard.enums.AlertSeverity;
import com.quantacus.dashboard.enums.AlertType;
import com.quantacus.dashboard.repository.ProductRepository;
import com.quantacus.dashboard.repository.ValidationAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final ValidationAlertRepository alertRepository;
    private final ProductRepository productRepository;

    // Validates every product in the list: computes quality score, marks duplicates,
    // and persists all generated alerts.  Called by PipelineService after extraction
    // and again (indirectly through ProductService.update) after manual edits.
    @Transactional
    public void validateAndScore(List<Product> products, Job job) {
        for (Product product : products) {

            // Wipe stale alerts before re-running so the count stays accurate
            alertRepository.deleteAllByProductId(product.getId());

            boolean isDuplicate = isDuplicate(product);
            product.setDuplicate(isDuplicate);
            product.setQualityScore(computeQualityScore(product));
            productRepository.save(product);

            List<ValidationAlert> alerts = generateAlerts(product, job, isDuplicate);
            alertRepository.saveAll(alerts);

            log.info("Product {} scored {} with {} alert(s) — duplicate={}",
                    product.getId(), product.getQualityScore(), alerts.size(), isDuplicate);
        }
    }

    // -------------------------------------------------------------------------
    // Quality score — called by both the pipeline and ProductService.update
    // -------------------------------------------------------------------------

    // Score breakdown (max 100):
    //   product_title present AND length > 20 chars  → +20
    //   description   present AND length > 50 chars  → +20
    //   image_url     present                         → +15
    //   price & mrp   both present AND price ≤ mrp   → +15
    //   brand         present                         → +10
    //   category      present                         → +10
    //   sku_id        present                         → +10
    public int computeQualityScore(Product p) {
        int score = 0;
        if (hasText(p.getProductTitle()) && p.getProductTitle().length() > 20) score += 20;
        if (hasText(p.getDescription())  && p.getDescription().length()  > 50) score += 20;
        if (hasText(p.getImageUrl()))                                           score += 15;
        if (p.getPrice() != null && p.getMrp() != null
                && p.getPrice().compareTo(p.getMrp()) <= 0)                     score += 15;
        if (hasText(p.getBrand()))                                              score += 10;
        if (hasText(p.getCategory()))                                           score += 10;
        if (hasText(p.getSkuId()))                                              score += 10;
        return score; // sum of weights above is exactly 100
    }

    // -------------------------------------------------------------------------
    // Alert generation
    // -------------------------------------------------------------------------

    private List<ValidationAlert> generateAlerts(Product p, Job job, boolean isDuplicate) {
        List<ValidationAlert> alerts = new ArrayList<>();

        // --- ERROR: required fields ---
        if (!hasText(p.getProductTitle()))
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.ERROR,
                    "product_title", "Product title is missing"));

        if (p.getPrice() == null)
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.ERROR,
                    "price", "Selling price is missing"));

        if (!hasText(p.getSkuId()))
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.ERROR,
                    "sku_id", "SKU ID is missing"));

        // --- WARNING: recommended fields ---
        if (!hasText(p.getBrand()))
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.WARNING,
                    "brand", "Brand is missing"));

        if (!hasText(p.getCategory()))
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.WARNING,
                    "category", "Category is missing"));

        if (!hasText(p.getDescription()))
            alerts.add(alert(p, job, AlertType.MISSING_FIELD, AlertSeverity.WARNING,
                    "description", "Description is missing"));

        if (!hasText(p.getImageUrl()))
            alerts.add(alert(p, job, AlertType.MISSING_IMAGE, AlertSeverity.WARNING,
                    "image_url", "Product image URL is missing"));

        // --- WARNING: description quality ---
        if (hasText(p.getDescription()) && p.getDescription().length() < 50)
            alerts.add(alert(p, job, AlertType.WEAK_DESCRIPTION, AlertSeverity.WARNING,
                    "description",
                    "Description is too short (" + p.getDescription().length()
                    + " chars) — aim for at least 50"));

        // --- Price rules ---
        if (p.getPrice() != null && p.getMrp() != null) {

            if (p.getPrice().compareTo(p.getMrp()) > 0)
                alerts.add(alert(p, job, AlertType.PRICE_ABOVE_MRP, AlertSeverity.ERROR,
                        "price",
                        "Selling price ₹" + p.getPrice()
                        + " exceeds MRP ₹" + p.getMrp()));

            BigDecimal floor = p.getMrp().multiply(BigDecimal.valueOf(0.3));
            if (p.getPrice().compareTo(floor) < 0)
                alerts.add(alert(p, job, AlertType.PRICE_BELOW_MRP_FLOOR, AlertSeverity.WARNING,
                        "price",
                        "Selling price is below 30 % of MRP — may be a data entry error"));
        }

        // --- Duplicate SKU ---
        if (isDuplicate)
            alerts.add(alert(p, job, AlertType.DUPLICATE_SKU, AlertSeverity.ERROR,
                    "sku_id",
                    "Another product with SKU '" + p.getSkuId() + "' already exists"));

        return alerts;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean isDuplicate(Product product) {
        if (!hasText(product.getSkuId())) return false;
        return productRepository.existsBySkuIdAndIdNot(product.getSkuId(), product.getId());
    }

    private ValidationAlert alert(Product product, Job job,
                                   AlertType type, AlertSeverity severity,
                                   String field, String message) {
        return ValidationAlert.builder()
                .product(product)
                .job(job)
                .alertType(type)
                .severity(severity)
                .fieldName(field)
                .message(message)
                .build();
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
