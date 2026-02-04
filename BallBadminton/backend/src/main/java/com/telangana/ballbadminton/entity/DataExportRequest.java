package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing user data export requests
 * Tracks requests for personal data export (GDPR compliance)
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Entity
@Table(name = "data_export_requests")
public class DataExportRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "export_format", length = 10)
    private String exportFormat; // JSON, CSV, PDF

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum RequestStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        EXPIRED
    }

    public DataExportRequest() {}

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
        if (status == null) {
            status = RequestStatus.PENDING;
        }
        // Data export files expire after 7 days
        if (expiryDate == null) {
            expiryDate = LocalDateTime.now().plusDays(7);
        }
    }
}
