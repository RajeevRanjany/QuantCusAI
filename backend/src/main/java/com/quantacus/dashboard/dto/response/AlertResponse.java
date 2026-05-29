package com.quantacus.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quantacus.dashboard.enums.AlertSeverity;
import com.quantacus.dashboard.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertResponse {

    private UUID id;
    private UUID productId;
    private UUID jobId;
    private AlertType alertType;
    private AlertSeverity severity;

    // The specific field that caused this alert (e.g. "price", "description")
    private String fieldName;

    private String message;
    private boolean resolved;
    private LocalDateTime createdAt;
}
