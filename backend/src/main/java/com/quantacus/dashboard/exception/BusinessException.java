package com.quantacus.dashboard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Thrown for domain-rule violations that are the caller's fault —
// e.g. uploading a CSV with missing columns, or deleting an active job.
// The status field lets each throw site choose the appropriate HTTP code.
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    // Defaults to 400 Bad Request
    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
