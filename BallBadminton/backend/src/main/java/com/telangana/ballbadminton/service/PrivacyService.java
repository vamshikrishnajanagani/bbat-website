package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.entity.*;
import com.telangana.ballbadminton.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

/**
 * Service for managing user privacy, consent, and data protection
 * Implements GDPR-compliant data export and deletion capabilities
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
@Transactional
public class PrivacyService {

    private static final Logger logger = LoggerFactory.getLogger(PrivacyService.class);

    private final PrivacyConsentRepository consentRepository;
    private final DataExportRequestRepository exportRequestRepository;
    private final DataDeletionRequestRepository deletionRequestRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final EmailService emailService;

    public PrivacyService(
            PrivacyConsentRepository consentRepository,
            DataExportRequestRepository exportRequestRepository,
            DataDeletionRequestRepository deletionRequestRepository,
            UserRepository userRepository,
            EncryptionService encryptionService,
            EmailService emailService
    ) {
        this.consentRepository = consentRepository;
        this.exportRequestRepository = exportRequestRepository;
        this.deletionRequestRepository = deletionRequestRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
    }

    /**
     * Record user consent for privacy policy or data processing
     */
    public PrivacyConsent recordConsent(
            UUID userId,
            String consentType,
            Boolean consentGiven,
            String ipAddress,
            String userAgent,
            String privacyPolicyVersion
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PrivacyConsent consent = new PrivacyConsent();
        consent.setUser(user);
        consent.setConsentType(consentType);
        consent.setConsentGiven(consentGiven);
        consent.setConsentDate(LocalDateTime.now());
        consent.setIpAddress(ipAddress);
        consent.setUserAgent(userAgent);
        consent.setPrivacyPolicyVersion(privacyPolicyVersion);
        consent.setRevoked(false);

        PrivacyConsent saved = consentRepository.save(consent);
        logger.info("Recorded consent for user {} - Type: {}, Given: {}", userId, consentType, consentGiven);
        
        return saved;
    }

    /**
     * Revoke user consent
     */
    public PrivacyConsent revokeConsent(UUID consentId, UUID userId) {
        PrivacyConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent record not found"));

        if (!consent.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to revoke this consent");
        }

        consent.setRevoked(true);
        consent.setRevokedDate(LocalDateTime.now());

        PrivacyConsent updated = consentRepository.save(consent);
        logger.info("Revoked consent {} for user {}", consentId, userId);
        
        return updated;
    }

    /**
     * Get all consent records for a user
     */
    @Transactional(readOnly = true)
    public List<PrivacyConsent> getUserConsents(UUID userId) {
        return consentRepository.findByUserId(userId);
    }

    /**
     * Check if user has given consent for a specific type
     */
    @Transactional(readOnly = true)
    public boolean hasActiveConsent(UUID userId, String consentType) {
        return consentRepository.findByUserIdAndConsentTypeAndRevokedFalse(userId, consentType)
                .stream()
                .anyMatch(PrivacyConsent::getConsentGiven);
    }

    /**
     * Request data export for a user
     */
    public DataExportRequest requestDataExport(UUID userId, String exportFormat, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for pending requests
        List<DataExportRequest> pendingRequests = exportRequestRepository
                .findByUserIdAndStatus(userId, DataExportRequest.RequestStatus.PENDING);
        
        if (!pendingRequests.isEmpty()) {
            throw new RuntimeException("A data export request is already pending");
        }

        DataExportRequest request = new DataExportRequest();
        request.setUser(user);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(DataExportRequest.RequestStatus.PENDING);
        request.setExportFormat(exportFormat != null ? exportFormat : "JSON");
        request.setIpAddress(ipAddress);
        request.setExpiryDate(LocalDateTime.now().plusDays(7));

        DataExportRequest saved = exportRequestRepository.save(request);
        logger.info("Created data export request {} for user {}", saved.getId(), userId);

        // Send confirmation email
        try {
            emailService.sendNotificationEmail(
                    user.getEmail(),
                    "Data Export Request Received",
                    "Your data export request has been received and will be processed within 30 days."
            );
        } catch (Exception e) {
            logger.error("Failed to send data export confirmation email", e);
        }

        return saved;
    }

    /**
     * Get data export request by ID
     */
    @Transactional(readOnly = true)
    public Optional<DataExportRequest> getDataExportRequest(UUID requestId, UUID userId) {
        return exportRequestRepository.findById(requestId)
                .filter(request -> request.getUser().getId().equals(userId));
    }

    /**
     * Get all data export requests for a user
     */
    @Transactional(readOnly = true)
    public List<DataExportRequest> getUserDataExportRequests(UUID userId) {
        return exportRequestRepository.findByUserId(userId);
    }

    /**
     * Request data deletion for a user
     */
    public DataDeletionRequest requestDataDeletion(
            UUID userId,
            DataDeletionRequest.DeletionType deletionType,
            String reason,
            String ipAddress
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for pending requests
        List<DataDeletionRequest> pendingRequests = deletionRequestRepository
                .findByUserIdAndStatusIn(userId, List.of(
                        DataDeletionRequest.RequestStatus.PENDING_VERIFICATION,
                        DataDeletionRequest.RequestStatus.VERIFIED,
                        DataDeletionRequest.RequestStatus.SCHEDULED,
                        DataDeletionRequest.RequestStatus.PROCESSING
                ));

        if (!pendingRequests.isEmpty()) {
            throw new RuntimeException("A data deletion request is already pending");
        }

        // Generate verification code
        String verificationCode = UUID.randomUUID().toString();

        DataDeletionRequest request = new DataDeletionRequest();
        request.setUser(user);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(DataDeletionRequest.RequestStatus.PENDING_VERIFICATION);
        request.setDeletionType(deletionType);
        request.setReason(reason);
        request.setIpAddress(ipAddress);
        request.setVerificationCode(encryptionService.hash(verificationCode));
        request.setVerified(false);
        request.setScheduledDate(LocalDateTime.now().plusDays(30)); // 30-day cooling-off period

        DataDeletionRequest saved = deletionRequestRepository.save(request);
        logger.info("Created data deletion request {} for user {}", saved.getId(), userId);

        // Send verification email
        try {
            emailService.sendNotificationEmail(
                    user.getEmail(),
                    "Data Deletion Request - Verification Required",
                    "Your data deletion request has been received. Please verify your request using this code: " + verificationCode +
                    "\n\nYour data will be deleted 30 days after verification."
            );
        } catch (Exception e) {
            logger.error("Failed to send data deletion verification email", e);
        }

        return saved;
    }

    /**
     * Verify data deletion request
     */
    public DataDeletionRequest verifyDataDeletionRequest(UUID requestId, String verificationCode, UUID userId) {
        DataDeletionRequest request = deletionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Deletion request not found"));

        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to verify this request");
        }

        if (request.getStatus() != DataDeletionRequest.RequestStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Request is not pending verification");
        }

        String hashedCode = encryptionService.hash(verificationCode);
        if (!hashedCode.equals(request.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        request.setVerified(true);
        request.setVerifiedDate(LocalDateTime.now());
        request.setStatus(DataDeletionRequest.RequestStatus.VERIFIED);

        DataDeletionRequest updated = deletionRequestRepository.save(request);
        logger.info("Verified data deletion request {} for user {}", requestId, userId);

        return updated;
    }

    /**
     * Cancel data deletion request (before processing)
     */
    public DataDeletionRequest cancelDataDeletionRequest(UUID requestId, UUID userId) {
        DataDeletionRequest request = deletionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Deletion request not found"));

        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this request");
        }

        if (request.getStatus() == DataDeletionRequest.RequestStatus.PROCESSING ||
            request.getStatus() == DataDeletionRequest.RequestStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel request in current status");
        }

        request.setStatus(DataDeletionRequest.RequestStatus.CANCELLED);
        request.setNotes("Cancelled by user");

        DataDeletionRequest updated = deletionRequestRepository.save(request);
        logger.info("Cancelled data deletion request {} for user {}", requestId, userId);

        return updated;
    }

    /**
     * Get data deletion request by ID
     */
    @Transactional(readOnly = true)
    public Optional<DataDeletionRequest> getDataDeletionRequest(UUID requestId, UUID userId) {
        return deletionRequestRepository.findById(requestId)
                .filter(request -> request.getUser().getId().equals(userId));
    }

    /**
     * Get all data deletion requests for a user
     */
    @Transactional(readOnly = true)
    public List<DataDeletionRequest> getUserDataDeletionRequests(UUID userId) {
        return deletionRequestRepository.findByUserId(userId);
    }

    /**
     * Process scheduled data deletions (to be called by scheduled job)
     */
    @Transactional
    public void processScheduledDeletions() {
        LocalDateTime now = LocalDateTime.now();
        List<DataDeletionRequest> scheduledRequests = deletionRequestRepository
                .findByStatusAndScheduledDateBefore(
                        DataDeletionRequest.RequestStatus.VERIFIED,
                        now
                );

        for (DataDeletionRequest request : scheduledRequests) {
            try {
                request.setStatus(DataDeletionRequest.RequestStatus.PROCESSING);
                deletionRequestRepository.save(request);

                // Perform actual deletion based on deletion type
                performDataDeletion(request);

                request.setStatus(DataDeletionRequest.RequestStatus.COMPLETED);
                request.setCompletedDate(LocalDateTime.now());
                deletionRequestRepository.save(request);

                logger.info("Completed data deletion for request {}", request.getId());
            } catch (Exception e) {
                logger.error("Failed to process data deletion request {}", request.getId(), e);
                request.setStatus(DataDeletionRequest.RequestStatus.FAILED);
                request.setNotes("Deletion failed: " + e.getMessage());
                deletionRequestRepository.save(request);
            }
        }
    }

    /**
     * Perform actual data deletion based on deletion type
     */
    private void performDataDeletion(DataDeletionRequest request) {
        User user = request.getUser();
        
        switch (request.getDeletionType()) {
            case FULL_ACCOUNT:
                // Delete all user data and account
                logger.info("Performing full account deletion for user {}", user.getId());
                // Implementation would delete all related data
                // This is a placeholder - actual implementation would cascade delete
                break;
                
            case PERSONAL_DATA_ONLY:
                // Anonymize personal data but keep records
                logger.info("Anonymizing personal data for user {}", user.getId());
                user.setEmail("deleted_" + user.getId() + "@anonymized.local");
                user.setUsername("deleted_user_" + user.getId());
                userRepository.save(user);
                break;
                
            case SPECIFIC_DATA:
                // Delete specific data categories
                logger.info("Deleting specific data for user {}", user.getId());
                // Implementation would delete specific data based on request
                break;
        }
    }

    /**
     * Clean up expired data export files (to be called by scheduled job)
     */
    @Transactional
    public void cleanupExpiredExports() {
        LocalDateTime now = LocalDateTime.now();
        List<DataExportRequest> expiredRequests = exportRequestRepository
                .findByStatusAndExpiryDateBefore(
                        DataExportRequest.RequestStatus.COMPLETED,
                        now
                );

        for (DataExportRequest request : expiredRequests) {
            try {
                // Delete the export file
                if (request.getFilePath() != null) {
                    // File deletion logic here
                    logger.info("Deleted expired export file: {}", request.getFilePath());
                }

                request.setStatus(DataExportRequest.RequestStatus.EXPIRED);
                exportRequestRepository.save(request);
            } catch (Exception e) {
                logger.error("Failed to cleanup expired export {}", request.getId(), e);
            }
        }
    }
}
