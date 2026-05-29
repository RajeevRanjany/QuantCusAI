package com.quantacus.dashboard.exception;

import java.util.UUID;

// Thrown when a requested entity (Job, Product, Alert, etc.) does not exist.
// Maps to HTTP 404 in GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
