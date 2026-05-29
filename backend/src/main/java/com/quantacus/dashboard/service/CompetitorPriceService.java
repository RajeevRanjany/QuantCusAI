package com.quantacus.dashboard.service;

import com.quantacus.dashboard.entity.CompetitorPrice;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.exception.ResourceNotFoundException;
import com.quantacus.dashboard.repository.CompetitorPriceRepository;
import com.quantacus.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitorPriceService {

    private final CompetitorPriceRepository priceRepository;
    private final ProductRepository productRepository;

    private static final List<String> PLATFORMS = List.of("Amazon", "Flipkart", "Meesho", "Snapdeal");

    // Called by the pipeline when a job completes
    @Transactional
    public List<CompetitorPrice> simulateAndSave(Product product) {
        List<CompetitorPrice> prices = simulate(product);
        List<CompetitorPrice> saved = priceRepository.saveAll(prices);
        log.info("Saved {} simulated competitor prices for product {}", saved.size(), product.getId());
        return saved;
    }

    // Called by the refresh endpoint — deletes existing rows then re-simulates
    @Transactional
    public List<CompetitorPrice> refresh(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        priceRepository.deleteAllByProductId(productId);
        log.info("Refreshing competitor prices for product {}", productId);
        return simulateAndSave(product);
    }

    public List<CompetitorPrice> getByProduct(UUID productId) {
        return priceRepository.findByProductId(productId);
    }

    // -------------------------------------------------------------------------
    // Simulation logic
    // -------------------------------------------------------------------------

    // Prices are seeded from the SKU (or product ID as fallback) so the same
    // product always produces the same four platform prices within a single run.
    // The "Refresh prices" button regenerates them with a time-based seed to
    // give the appearance of live data changing.
    private List<CompetitorPrice> simulate(Product product) {
        BigDecimal base = product.getPrice() != null
                ? product.getPrice()
                : BigDecimal.valueOf(1_000);

        long seed = product.getSkuId() != null
                ? product.getSkuId().hashCode()
                : product.getId().hashCode();

        Random random = new Random(seed);

        return PLATFORMS.stream().map(platform -> {
            // Each platform price is between 85 % and 120 % of the seller's price
            double factor = 0.85 + (random.nextDouble() * 0.35);
            BigDecimal competitorPrice = base
                    .multiply(BigDecimal.valueOf(factor))
                    .setScale(2, RoundingMode.HALF_UP);

            return CompetitorPrice.builder()
                    .product(product)
                    .platform(platform)
                    .competitorTitle(product.getProductTitle())
                    .price(competitorPrice)
                    .currency("INR")
                    .url("https://www." + platform.toLowerCase() + ".com/search?q="
                            + encodeForUrl(product.getProductTitle()))
                    .simulated(true)
                    .fetchedAt(LocalDateTime.now())
                    .build();
        }).toList();
    }

    private String encodeForUrl(String title) {
        if (title == null || title.isBlank()) return "product";
        return title.replace(" ", "+");
    }
}
