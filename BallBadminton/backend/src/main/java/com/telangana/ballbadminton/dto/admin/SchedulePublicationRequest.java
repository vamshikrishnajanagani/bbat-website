package com.telangana.ballbadminton.dto.admin;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request DTO for scheduling content publication
 * 
 * Allows administrators to schedule content for
 * future publication at a specific date and time
 * 
 * Requirements: 6.4
 */
public class SchedulePublicationRequest {

    @NotNull(message = "Entity type is required")
    private BulkOperationRequest.EntityType entityType;

    @NotNull(message = "Entity ID is required")
    private String entityId;

    @NotNull(message = "Scheduled publication date is required")
    @Future(message = "Scheduled publication date must be in the future")
    private LocalDateTime scheduledPublicationDate;

    private boolean autoPublish = true;

    // Constructors
    public SchedulePublicationRequest() {}

    public SchedulePublicationRequest(BulkOperationRequest.EntityType entityType, 
                                     String entityId, 
                                     LocalDateTime scheduledPublicationDate) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.scheduledPublicationDate = scheduledPublicationDate;
    }

    // Getters and Setters
    public BulkOperationRequest.EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BulkOperationRequest.EntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getScheduledPublicationDate() {
        return scheduledPublicationDate;
    }

    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) {
        this.scheduledPublicationDate = scheduledPublicationDate;
    }

    public boolean isAutoPublish() {
        return autoPublish;
    }

    public void setAutoPublish(boolean autoPublish) {
        this.autoPublish = autoPublish;
    }
}
