package com.quantacus.dashboard.service;

import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.enums.ExtractionSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ExtractionService {

    // Confidence must reach this threshold for extraction to be considered complete.
    // Below this value the pipeline pauses and asks the user for a CSV or manual input.
    private static final double CONFIDENCE_THRESHOLD = 0.75;

    public record ExtractionResult(
            List<Product> products,
            double confidence,
            boolean incomplete
    ) {}

    // Entry point called by PipelineService.
    // In production this would: pull frames from the video, run OCR, call an AI
    // vision model, and parse the response into product fields.
    // Here it returns a deterministic mock so the full pipeline can be exercised
    // without any external dependencies.
    public ExtractionResult extract(Job job) {
        log.info("Starting mocked video extraction for job {}", job.getId());

        simulateProcessingDelay();

        double confidence = resolveConfidence(job.getVideoPath());
        Product product = buildMockProduct(job, confidence);

        log.info("Extraction complete for job {} — confidence={}, incomplete={}",
                job.getId(), confidence, confidence < CONFIDENCE_THRESHOLD);

        return new ExtractionResult(
                List.of(product),
                confidence,
                confidence < CONFIDENCE_THRESHOLD
        );
    }

    // Builds a realistic mock product so the dashboard has something to display.
    // All fields that a real video analysis might miss are intentionally left null
    // on the low-confidence path so the validation alerts fire correctly.
    private Product buildMockProduct(Job job, double confidence) {
        Product.ProductBuilder builder = Product.builder()
                .job(job)
                .skuId("SKU-" + System.currentTimeMillis())
                .productTitle("Nike Blue Lightweight Running Shoes")
                .brand("Nike")
                .category("Footwear")
                .price(new BigDecimal("2999.00"))
                .mrp(new BigDecimal("4999.00"))
                .availability("in_stock")
                .color("Blue")
                .material("Mesh")
                .extractionSource(ExtractionSource.VIDEO);

        // High-confidence path fills all optional fields
        if (confidence >= CONFIDENCE_THRESHOLD) {
            builder
                .description("Lightweight Nike running shoes with breathable mesh upper, "
                           + "ideal for daily training and casual wear.")
                .imageUrl("https://example.com/images/nike-blue-running.jpg")
                .productUrl("https://www.flipkart.com/nike-blue-running-shoes")
                .size("UK 9");
        }

        return builder.build();
    }

    // Simulates confidence based on filename so developers can test both flows:
    //   - Upload a file with "low" in the name → triggers the fallback flow
    //   - Any other file → extraction succeeds
    private double resolveConfidence(String videoPath) {
        if (videoPath != null && videoPath.toLowerCase().contains("low")) {
            return 0.50;
        }
        return 0.85;
    }

    private void simulateProcessingDelay() {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
