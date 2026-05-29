package com.quantacus.dashboard.service;

import com.quantacus.dashboard.dto.response.AlertResponse;
import com.quantacus.dashboard.entity.ValidationAlert;
import com.quantacus.dashboard.exception.ResourceNotFoundException;
import com.quantacus.dashboard.repository.ValidationAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final ValidationAlertRepository alertRepository;

    @Transactional(readOnly = true)
    public List<AlertResponse> getByJob(UUID jobId) {
        return alertRepository.findByJobIdOrderBySeverity(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getByProduct(UUID productId) {
        return alertRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AlertResponse resolve(UUID alertId) {
        ValidationAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));
        alert.setResolved(true);
        return toResponse(alertRepository.save(alert));
    }

    public AlertResponse toResponse(ValidationAlert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .productId(a.getProduct().getId())
                .jobId(a.getJob().getId())
                .alertType(a.getAlertType())
                .severity(a.getSeverity())
                .fieldName(a.getFieldName())
                .message(a.getMessage())
                .resolved(a.isResolved())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
