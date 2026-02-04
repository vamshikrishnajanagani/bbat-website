package com.telangana.ballbadminton.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for bulk operations on content entities
 * 
 * Supports bulk create, update, and delete operations
 * for efficient content management
 * 
 * Requirements: 6.3, 9.5
 */
public class BulkOperationRequest {

    @NotNull(message = "Operation type is required")
    private OperationType operation;

    @NotNull(message = "Entity type is required")
    private EntityType entityType;

    @NotEmpty(message = "At least one entity ID is required for update/delete operations")
    private List<String> entityIds;

    private List<Map<String, Object>> entities;

    private Map<String, Object> updateFields;

    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE,
        PUBLISH,
        UNPUBLISH
    }

    public enum EntityType {
        MEMBER,
        PLAYER,
        TOURNAMENT,
        NEWS_ARTICLE,
        MEDIA_ITEM,
        DISTRICT,
        DOWNLOAD
    }

    // Constructors
    public BulkOperationRequest() {}

    public BulkOperationRequest(OperationType operation, EntityType entityType) {
        this.operation = operation;
        this.entityType = entityType;
    }

    // Getters and Setters
    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public List<String> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<String> entityIds) {
        this.entityIds = entityIds;
    }

    public List<Map<String, Object>> getEntities() {
        return entities;
    }

    public void setEntities(List<Map<String, Object>> entities) {
        this.entities = entities;
    }

    public Map<String, Object> getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(Map<String, Object> updateFields) {
        this.updateFields = updateFields;
    }
}
