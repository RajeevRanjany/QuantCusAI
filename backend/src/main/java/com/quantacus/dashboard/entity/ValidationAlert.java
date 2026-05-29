package com.quantacus.dashboard.entity;

import com.quantacus.dashboard.enums.AlertSeverity;
import com.quantacus.dashboard.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "validation_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    // Which field triggered this alert (e.g. "price", "description")
    private String fieldName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Builder.Default
    private boolean resolved = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
