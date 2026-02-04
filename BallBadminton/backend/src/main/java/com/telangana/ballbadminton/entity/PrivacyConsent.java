package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing user privacy consent
 * Tracks user consent for data processing and privacy policy acceptance
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Entity
@Table(name = "privacy_consents")
public class PrivacyConsent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "consent_type", nullable = false, length = 50)
    private String consentType; // e.g., "PRIVACY_POLICY", "DATA_PROCESSING", "MARKETING"

    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    @Column(name = "consent_date", nullable = false)
    private LocalDateTime consentDate;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "privacy_policy_version", length = 20)
    private String privacyPolicyVersion;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "revoked_date")
    private LocalDateTime revokedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public PrivacyConsent() {}

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConsentType() {
        return consentType;
    }

    public void setConsentType(String consentType) {
        this.consentType = consentType;
    }

    public Boolean getConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }

    public LocalDateTime getConsentDate() {
        return consentDate;
    }

    public void setConsentDate(LocalDateTime consentDate) {
        this.consentDate = consentDate;
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

    public String getPrivacyPolicyVersion() {
        return privacyPolicyVersion;
    }

    public void setPrivacyPolicyVersion(String privacyPolicyVersion) {
        this.privacyPolicyVersion = privacyPolicyVersion;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public LocalDateTime getRevokedDate() {
        return revokedDate;
    }

    public void setRevokedDate(LocalDateTime revokedDate) {
        this.revokedDate = revokedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (consentDate == null) {
            consentDate = LocalDateTime.now();
        }
        if (revoked == null) {
            revoked = false;
        }
    }
}
