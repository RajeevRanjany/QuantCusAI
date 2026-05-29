package com.quantacus.dashboard.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.enums.ExtractionSource;
import com.quantacus.dashboard.exception.BusinessException;
import com.quantacus.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

    private final ProductRepository productRepository;

    // Exact column names expected in the uploaded CSV (case-insensitive after normalisation)
    private static final List<String> REQUIRED_HEADERS = List.of(
            "sku_id", "product_title", "description", "brand",
            "category", "price", "mrp", "image_url",
            "product_url", "availability", "color", "size", "material"
    );

    @Transactional
    public List<Product> parseAndSave(MultipartFile file, Job job) {
        List<Product> products = parse(file, job);
        return productRepository.saveAll(products);
    }

    public List<Product> parse(MultipartFile file, Job job) {
        validateContentType(file);

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<String[]> rows = reader.readAll();

            if (rows.isEmpty()) {
                throw new BusinessException("CSV file is empty");
            }

            String[] rawHeaders = rows.get(0);
            String[] headers = normaliseHeaders(rawHeaders);
            validateHeaders(headers);

            List<Product> products = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (isBlankRow(row)) continue;
                products.add(mapRow(headers, row, job));
            }

            if (products.isEmpty()) {
                throw new BusinessException("CSV has headers but no data rows");
            }

            log.info("Parsed {} product(s) from CSV for job {}", products.size(), job.getId());
            return products;

        } catch (BusinessException e) {
            throw e;
        } catch (IOException | CsvException e) {
            log.error("CSV parsing failed for job {}", job.getId(), e);
            throw new BusinessException("Failed to read CSV: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void validateContentType(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && !name.toLowerCase().endsWith(".csv")) {
            throw new BusinessException("Only .csv files are accepted for the fallback upload");
        }
    }

    private String[] normaliseHeaders(String[] raw) {
        return Arrays.stream(raw)
                .map(String::trim)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }

    private void validateHeaders(String[] headers) {
        List<String> present = Arrays.asList(headers);
        List<String> missing = REQUIRED_HEADERS.stream()
                .filter(required -> !present.contains(required))
                .toList();

        if (!missing.isEmpty()) {
            throw new BusinessException("CSV is missing required columns: " + missing);
        }
    }

    private boolean isBlankRow(String[] row) {
        return Arrays.stream(row).allMatch(cell -> cell == null || cell.isBlank());
    }

    private Product mapRow(String[] headers, String[] row, Job job) {
        Map<String, String> record = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            record.put(headers[i], i < row.length ? row[i].trim() : "");
        }

        return Product.builder()
                .job(job)
                .skuId(blankToNull(record.get("sku_id")))
                .productTitle(blankToNull(record.get("product_title")))
                .description(blankToNull(record.get("description")))
                .brand(blankToNull(record.get("brand")))
                .category(blankToNull(record.get("category")))
                .price(parseBigDecimal(record.get("price")))
                .mrp(parseBigDecimal(record.get("mrp")))
                .imageUrl(blankToNull(record.get("image_url")))
                .productUrl(blankToNull(record.get("product_url")))
                .availability(blankToNull(record.get("availability")))
                .color(blankToNull(record.get("color")))
                .size(blankToNull(record.get("size")))
                .material(blankToNull(record.get("material")))
                .extractionSource(ExtractionSource.CSV)
                .build();
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null; // Treat unparseable prices as missing — validation will flag them
        }
    }
}
