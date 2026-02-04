package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.PrivacyConsent;
import com.telangana.ballbadminton.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PrivacyConsent entity
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface PrivacyConsentRepository extends JpaRepository<PrivacyConsent, UUID> {

    /**
     * Find all consents for a user
     */
    List<PrivacyConsent> findByUserOrderByConsentDateDesc(User user);

    /**
     * Find all consents for a user by user ID
     */
    List<PrivacyConsent> findByUserId(UUID userId);

    /**
     * Find active consent by user and type
     */
    @Query("SELECT pc FROM PrivacyConsent pc WHERE pc.user = :user AND pc.consentType = :consentType " +
           "AND pc.revoked = false ORDER BY pc.consentDate DESC")
    Optional<PrivacyConsent> findActiveConsentByUserAndType(
        @Param("user") User user, 
        @Param("consentType") String consentType
    );

    /**
     * Find consents by user ID and consent type where not revoked
     */
    List<PrivacyConsent> findByUserIdAndConsentTypeAndRevokedFalse(UUID userId, String consentType);

    /**
     * Check if user has given consent for a specific type
     */
    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END FROM PrivacyConsent pc " +
           "WHERE pc.user = :user AND pc.consentType = :consentType " +
           "AND pc.consentGiven = true AND pc.revoked = false")
    boolean hasActiveConsent(@Param("user") User user, @Param("consentType") String consentType);

    /**
     * Find all consents by type
     */
    List<PrivacyConsent> findByConsentTypeOrderByConsentDateDesc(String consentType);
}
