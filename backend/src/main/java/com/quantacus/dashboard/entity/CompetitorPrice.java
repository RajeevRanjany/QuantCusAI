package com.quantacus.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "competitor_prices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String platform;

    @Column(columnDefinition = "TEXT")
    private String competitorTitle;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    private String currency = "INR";

    @Column(columnDefinition = "TEXT")
    private String url;

    // Always true in this assignment (simulated data)
    @Builder.Default
    private boolean simulated = true;

    private LocalDateTime fetchedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
