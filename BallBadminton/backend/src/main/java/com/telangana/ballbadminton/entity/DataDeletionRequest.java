package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing user data deletion requests
 * Tracks requests for personal data deletion (Right to be Forgotten)
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Entity
@Table(name = "data_deletion_requests")
public class DataDeletionRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "deletion_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DeletionType deletionType;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "verification_code", length = 100)
    private String verificationCode;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum RequestStatus {
        PENDING_VERIFICATION,
        VERIFIED,
        SCHEDULED,
        PROCESSING,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public enum DeletionType {
        FULL_ACCOUNT,      // Delete entire account and all data
        PERSONAL_DATA_ONLY, // Keep anonymized records
        SPECIFIC_DATA      // Delete specific data categories
    }

    public DataDeletionRequest() {}

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

    public DeletionType getDeletionType() {
        return deletionType;
    }

    public void setDeletionType(DeletionType deletionType) {
        this.deletionType = deletionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(LocalDateTime verifiedDate) {
        this.verifiedDate = verifiedDate;
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
            status = RequestStatus.PENDING_VERIFICATION;
        }
        if (verified == null) {
            verified = false;
        }
        // Schedule deletion 30 days after request (cooling-off period)
        if (scheduledDate == null) {
            scheduledDate = LocalDateTime.now().plusDays(30);
        }
    }
}
