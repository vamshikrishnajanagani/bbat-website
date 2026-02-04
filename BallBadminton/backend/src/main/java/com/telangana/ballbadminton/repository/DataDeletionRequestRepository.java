package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.DataDeletionRequest;
import com.telangana.ballbadminton.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DataDeletionRequest entity
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface DataDeletionRequestRepository extends JpaRepository<DataDeletionRequest, UUID> {

    /**
     * Find all deletion requests for a user
     */
    List<DataDeletionRequest> findByUserOrderByRequestDateDesc(User user);

    /**
     * Find all deletion requests for a user by user ID
     */
    List<DataDeletionRequest> findByUserId(UUID userId);

    /**
     * Find deletion requests by user ID and status in list
     */
    List<DataDeletionRequest> findByUserIdAndStatusIn(UUID userId, List<DataDeletionRequest.RequestStatus> statuses);

    /**
     * Find deletion requests by status
     */
    List<DataDeletionRequest> findByStatusOrderByRequestDateAsc(DataDeletionRequest.RequestStatus status);

    /**
     * Find deletion requests by status and scheduled date before
     */
    List<DataDeletionRequest> findByStatusAndScheduledDateBefore(
            DataDeletionRequest.RequestStatus status,
            LocalDateTime scheduledDate
    );

    /**
     * Find scheduled deletion requests ready for processing
     */
    @Query("SELECT ddr FROM DataDeletionRequest ddr WHERE ddr.status = 'SCHEDULED' " +
           "AND ddr.scheduledDate <= :now AND ddr.verified = true")
    List<DataDeletionRequest> findScheduledForDeletion(@Param("now") LocalDateTime now);

    /**
     * Find active deletion request for user
     */
    @Query("SELECT ddr FROM DataDeletionRequest ddr WHERE ddr.user = :user " +
           "AND ddr.status NOT IN ('COMPLETED', 'CANCELLED', 'FAILED') " +
           "ORDER BY ddr.requestDate DESC")
    Optional<DataDeletionRequest> findActiveRequestByUser(@Param("user") User user);

    /**
     * Find deletion request by verification code
     */
    Optional<DataDeletionRequest> findByVerificationCode(String verificationCode);
}
