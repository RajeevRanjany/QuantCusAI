package com.quantacus.dashboard.entity;

import com.quantacus.dashboard.enums.JobStatus;
import com.quantacus.dashboard.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    private boolean enhanceTitle;

    // Path on local disk where the uploaded video is stored
    @Column(columnDefinition = "TEXT")
    private String videoPath;

    // Path on local disk where the uploaded CSV is stored
    @Column(columnDefinition = "TEXT")
    private String csvPath;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ValidationAlert> alerts = new ArrayList<>();
}
