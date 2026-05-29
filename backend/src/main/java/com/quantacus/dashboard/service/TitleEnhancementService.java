package com.quantacus.dashboard.service;

import com.quantacus.dashboard.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TitleEnhancementService {

    // In production: replace this method body with an OpenAI GPT-4 API call.
    // The prompt would include the product's raw title and extracted attributes,
    // and the response would be a single improved title string.
    //
    // Mock strategy: construct a richer title by joining the available structured
    // fields in a meaningful order.  The result will be noticeably better than
    // the raw title when brand, color, material, or size are present.
    public String enhance(Product product) {
        log.info("Generating enhanced title for product {}", product.getId());

        StringBuilder title = new StringBuilder();

        // Pattern: <Brand> <Color> <Core Title> with <Material> Upper[, Size <Size>]
        if (hasText(product.getBrand()))    title.append(product.getBrand()).append(" ");
        if (hasText(product.getColor()))    title.append(product.getColor()).append(" ");

        String core = hasText(product.getProductTitle())
                ? product.getProductTitle()
                : hasText(product.getCategory()) ? product.getCategory() : "Product";

        title.append(core);

        if (hasText(product.getMaterial()))
            title.append(" with ").append(product.getMaterial()).append(" Upper");

        if (hasText(product.getSize()))
            title.append(", Size ").append(product.getSize());

        String enhanced = title.toString().trim();
        log.info("Enhanced title: \"{}\"", enhanced);
        return enhanced;
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
