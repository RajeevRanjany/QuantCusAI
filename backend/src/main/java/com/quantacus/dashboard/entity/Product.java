package com.quantacus.dashboard.entity;

import com.quantacus.dashboard.enums.ExtractionSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private String skuId;

    @Column(columnDefinition = "TEXT")
    private String productTitle;

    @Column(columnDefinition = "TEXT")
    private String enhancedTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String brand;
    private String category;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String productUrl;

    private String availability;
    private String color;
    private String size;
    private String material;

    private Integer qualityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtractionSource extractionSource;

    @Builder.Default
    private boolean duplicate = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ValidationAlert> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CompetitorPrice> competitorPrices = new HashSet<>();
}
