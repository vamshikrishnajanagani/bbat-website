package com.telangana.ballbadminton.dto.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for bulk operations
 * 
 * Contains results of bulk operations including
 * success count, failure count, and detailed results
 * 
 * Requirements: 6.3, 9.5
 */
public class BulkOperationResponse {

    private String operationId;
    private BulkOperationRequest.OperationType operation;
    private BulkOperationRequest.EntityType entityType;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private LocalDateTime timestamp;
    private List<OperationResult> results;

    public BulkOperationResponse() {
        this.results = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public static class OperationResult {
        private String entityId;
        private boolean success;
        private String message;
        private String errorDetails;

        public OperationResult() {}

        public OperationResult(String entityId, boolean success, String message) {
            this.entityId = entityId;
            this.success = success;
            this.message = message;
        }

        // Getters and Setters
        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getErrorDetails() {
            return errorDetails;
        }

        public void setErrorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
        }
    }

    // Getters and Setters
    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public BulkOperationRequest.OperationType getOperation() {
        return operation;
    }

    public void setOperation(BulkOperationRequest.OperationType operation) {
        this.operation = operation;
    }

    public BulkOperationRequest.EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BulkOperationRequest.EntityType entityType) {
        this.entityType = entityType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<OperationResult> getResults() {
        return results;
    }

    public void setResults(List<OperationResult> results) {
        this.results = results;
    }

    public void addResult(OperationResult result) {
        this.results.add(result);
        if (result.isSuccess()) {
            this.successCount++;
        } else {
            this.failureCount++;
        }
    }
}
