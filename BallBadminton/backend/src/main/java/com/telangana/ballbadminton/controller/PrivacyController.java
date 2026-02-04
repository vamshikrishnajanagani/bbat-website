package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.entity.DataDeletionRequest;
import com.telangana.ballbadminton.entity.DataExportRequest;
import com.telangana.ballbadminton.entity.PrivacyConsent;
import com.telangana.ballbadminton.service.PrivacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for privacy and data protection operations
 * Provides endpoints for consent management, data export, and data deletion
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/privacy")
@Tag(name = "Privacy", description = "Privacy and data protection endpoints")
public class PrivacyController {

    private final PrivacyService privacyService;

    public PrivacyController(PrivacyService privacyService) {
        this.privacyService = privacyService;
    }

    /**
     * Record user consent
     */
    @PostMapping("/consent")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Record user consent", description = "Record user consent for privacy policy or data processing")
    public ResponseEntity<PrivacyConsent> recordConsent(
            @RequestBody ConsentRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        PrivacyConsent consent = privacyService.recordConsent(
                userId,
                request.getConsentType(),
                request.getConsentGiven(),
                ipAddress,
                userAgent,
                request.getPrivacyPolicyVersion()
        );

        return ResponseEntity.ok(consent);
    }

    /**
     * Revoke user consent
     */
    @PostMapping("/consent/{consentId}/revoke")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Revoke consent", description = "Revoke a previously given consent")
    public ResponseEntity<PrivacyConsent> revokeConsent(
            @PathVariable UUID consentId,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        PrivacyConsent consent = privacyService.revokeConsent(consentId, userId);
        return ResponseEntity.ok(consent);
    }

    /**
     * Get user's consent records
     */
    @GetMapping("/consent")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user consents", description = "Get all consent records for the authenticated user")
    public ResponseEntity<List<PrivacyConsent>> getUserConsents(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<PrivacyConsent> consents = privacyService.getUserConsents(userId);
        return ResponseEntity.ok(consents);
    }

    /**
     * Check if user has active consent for a specific type
     */
    @GetMapping("/consent/check/{consentType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check consent status", description = "Check if user has active consent for a specific type")
    public ResponseEntity<Map<String, Boolean>> checkConsent(
            @PathVariable String consentType,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        boolean hasConsent = privacyService.hasActiveConsent(userId, consentType);
        return ResponseEntity.ok(Map.of("hasConsent", hasConsent));
    }

    /**
     * Request data export
     */
    @PostMapping("/data-export")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Request data export", description = "Request export of user's personal data")
    public ResponseEntity<DataExportRequest> requestDataExport(
            @RequestBody DataExportRequestDto request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        String ipAddress = getClientIpAddress(httpRequest);

        DataExportRequest exportRequest = privacyService.requestDataExport(
                userId,
                request.getExportFormat(),
                ipAddress
        );

        return ResponseEntity.ok(exportRequest);
    }

    /**
     * Get data export request status
     */
    @GetMapping("/data-export/{requestId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get data export request", description = "Get status of a data export request")
    public ResponseEntity<DataExportRequest> getDataExportRequest(
            @PathVariable UUID requestId,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        return privacyService.getDataExportRequest(requestId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all data export requests for user
     */
    @GetMapping("/data-export")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's data export requests", description = "Get all data export requests for the authenticated user")
    public ResponseEntity<List<DataExportRequest>> getUserDataExportRequests(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<DataExportRequest> requests = privacyService.getUserDataExportRequests(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Request data deletion
     */
    @PostMapping("/data-deletion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Request data deletion", description = "Request deletion of user's personal data")
    public ResponseEntity<DataDeletionRequest> requestDataDeletion(
            @RequestBody DataDeletionRequestDto request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        String ipAddress = getClientIpAddress(httpRequest);

        DataDeletionRequest deletionRequest = privacyService.requestDataDeletion(
                userId,
                request.getDeletionType(),
                request.getReason(),
                ipAddress
        );

        return ResponseEntity.ok(deletionRequest);
    }

    /**
     * Verify data deletion request
     */
    @PostMapping("/data-deletion/{requestId}/verify")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Verify data deletion request", description = "Verify a data deletion request with verification code")
    public ResponseEntity<DataDeletionRequest> verifyDataDeletionRequest(
            @PathVariable UUID requestId,
            @RequestBody VerificationRequest request,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        DataDeletionRequest deletionRequest = privacyService.verifyDataDeletionRequest(
                requestId,
                request.getVerificationCode(),
                userId
        );
        return ResponseEntity.ok(deletionRequest);
    }

    /**
     * Cancel data deletion request
     */
    @PostMapping("/data-deletion/{requestId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel data deletion request", description = "Cancel a pending data deletion request")
    public ResponseEntity<DataDeletionRequest> cancelDataDeletionRequest(
            @PathVariable UUID requestId,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        DataDeletionRequest deletionRequest = privacyService.cancelDataDeletionRequest(requestId, userId);
        return ResponseEntity.ok(deletionRequest);
    }

    /**
     * Get data deletion request status
     */
    @GetMapping("/data-deletion/{requestId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get data deletion request", description = "Get status of a data deletion request")
    public ResponseEntity<DataDeletionRequest> getDataDeletionRequest(
            @PathVariable UUID requestId,
            Authentication authentication
    ) {
        UUID userId = getUserIdFromAuthentication(authentication);
        return privacyService.getDataDeletionRequest(requestId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all data deletion requests for user
     */
    @GetMapping("/data-deletion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's data deletion requests", description = "Get all data deletion requests for the authenticated user")
    public ResponseEntity<List<DataDeletionRequest>> getUserDataDeletionRequests(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<DataDeletionRequest> requests = privacyService.getUserDataDeletionRequests(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get privacy policy (public endpoint)
     */
    @GetMapping("/policy")
    @Operation(summary = "Get privacy policy", description = "Get the current privacy policy")
    public ResponseEntity<Map<String, Object>> getPrivacyPolicy() {
        // This would typically fetch from database or configuration
        Map<String, Object> policy = Map.of(
                "version", "1.0.0",
                "effectiveDate", "2024-01-01",
                "content", "Privacy policy content here...",
                "lastUpdated", "2024-01-01"
        );
        return ResponseEntity.ok(policy);
    }

    // Helper methods

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        // Extract user ID from authentication principal
        // This assumes the principal contains user details with ID
        return UUID.randomUUID(); // Placeholder - implement based on your authentication setup
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // DTOs

    public static class ConsentRequest {
        private String consentType;
        private Boolean consentGiven;
        private String privacyPolicyVersion;

        public String getConsentType() { return consentType; }
        public void setConsentType(String consentType) { this.consentType = consentType; }
        public Boolean getConsentGiven() { return consentGiven; }
        public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }
        public String getPrivacyPolicyVersion() { return privacyPolicyVersion; }
        public void setPrivacyPolicyVersion(String privacyPolicyVersion) { this.privacyPolicyVersion = privacyPolicyVersion; }
    }

    public static class DataExportRequestDto {
        private String exportFormat;

        public String getExportFormat() { return exportFormat; }
        public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }
    }

    public static class DataDeletionRequestDto {
        private DataDeletionRequest.DeletionType deletionType;
        private String reason;

        public DataDeletionRequest.DeletionType getDeletionType() { return deletionType; }
        public void setDeletionType(DataDeletionRequest.DeletionType deletionType) { this.deletionType = deletionType; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class VerificationRequest {
        private String verificationCode;

        public String getVerificationCode() { return verificationCode; }
        public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    }
}
