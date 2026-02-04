package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an audit log entry for tracking all operations in the system
 * 
 * This entity provides comprehensive audit trails for:
 * - User actions and operations
 * - Data modifications (create, update, delete)
 * - Security events (login, logout, access denied)
 * - System events (configuration changes, errors)
 * 
 * Requirements: 6.2, 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_entity_type", columnList = "entity_type"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_severity", columnList = "severity"),
    @Index(name = "idx_audit_status", columnList = "status")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User who performed the action (null for system actions)
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Username for quick reference
     */
    @Column(name = "username", length = 100)
    private String username;

    /**
     * Type of action performed
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;

    /**
     * Type of entity affected (e.g., Member, Player, Tournament)
     */
    @Column(name = "entity_type", length = 100)
    private String entityType;

    /**
     * ID of the entity affected
     */
    @Column(name = "entity_id", length = 100)
    private String entityId;

    /**
     * Detailed description of the action
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Old values before the change (JSON format)
     */
    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    /**
     * New values after the change (JSON format)
     */
    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    /**
     * IP address of the client
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent string
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Request method (GET, POST, PUT, DELETE)
     */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /**
     * Request URL
     */
    @Column(name = "request_url", length = 500)
    private String requestUrl;

    /**
     * HTTP status code
     */
    @Column(name = "status_code")
    private Integer statusCode;

    /**
     * Execution time in milliseconds
     */
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    /**
     * Severity level of the audit event
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AuditSeverity severity;

    /**
     * Status of the operation
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditStatus status;

    /**
     * Error message if operation failed
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Stack trace if error occurred
     */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    /**
     * Additional metadata (JSON format)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Timestamp when the action occurred
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Session ID for tracking user sessions
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;

    /**
     * Correlation ID for tracking related operations
     */
    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    // Constructors
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
        this.severity = AuditSeverity.INFO;
        this.status = AuditStatus.SUCCESS;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public AuditSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AuditSeverity severity) {
        this.severity = severity;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Enum for audit action types
     */
    public enum AuditAction {
        // Authentication actions
        LOGIN,
        LOGOUT,
        LOGIN_FAILED,
        TOKEN_REFRESH,
        PASSWORD_CHANGE,
        PASSWORD_RESET,
        
        // CRUD operations
        CREATE,
        READ,
        UPDATE,
        DELETE,
        BULK_CREATE,
        BULK_UPDATE,
        BULK_DELETE,
        
        // Security events
        ACCESS_DENIED,
        PERMISSION_DENIED,
        UNAUTHORIZED_ACCESS,
        SUSPICIOUS_ACTIVITY,
        
        // Data operations
        EXPORT,
        IMPORT,
        BACKUP,
        RESTORE,
        
        // Privacy operations
        CONSENT_GIVEN,
        CONSENT_REVOKED,
        DATA_EXPORT_REQUEST,
        DATA_DELETION_REQUEST,
        
        // System events
        CONFIGURATION_CHANGE,
        SYSTEM_ERROR,
        SYSTEM_WARNING,
        CACHE_CLEAR,
        
        // File operations
        FILE_UPLOAD,
        FILE_DOWNLOAD,
        FILE_DELETE
    }

    /**
     * Enum for audit severity levels
     */
    public enum AuditSeverity {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    /**
     * Enum for audit status
     */
    public enum AuditStatus {
        SUCCESS,
        FAILURE,
        PARTIAL_SUCCESS,
        IN_PROGRESS
    }
}
