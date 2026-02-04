package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.DataExportRequest;
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
 * Repository for DataExportRequest entity
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface DataExportRequestRepository extends JpaRepository<DataExportRequest, UUID> {

    /**
     * Find all export requests for a user
     */
    List<DataExportRequest> findByUserOrderByRequestDateDesc(User user);

    /**
     * Find all export requests for a user by user ID
     */
    List<DataExportRequest> findByUserId(UUID userId);

    /**
     * Find export requests by user ID and status
     */
    List<DataExportRequest> findByUserIdAndStatus(UUID userId, DataExportRequest.RequestStatus status);

    /**
     * Find pending export requests
     */
    List<DataExportRequest> findByStatusOrderByRequestDateAsc(DataExportRequest.RequestStatus status);

    /**
     * Find export requests by status and expiry date before
     */
    List<DataExportRequest> findByStatusAndExpiryDateBefore(
            DataExportRequest.RequestStatus status,
            LocalDateTime expiryDate
    );

    /**
     * Find expired export requests
     */
    @Query("SELECT der FROM DataExportRequest der WHERE der.status = 'COMPLETED' " +
           "AND der.expiryDate < :now")
    List<DataExportRequest> findExpiredRequests(@Param("now") LocalDateTime now);

    /**
     * Find active export request for user
     */
    @Query("SELECT der FROM DataExportRequest der WHERE der.user = :user " +
           "AND der.status IN ('PENDING', 'PROCESSING') ORDER BY der.requestDate DESC")
    Optional<DataExportRequest> findActiveRequestByUser(@Param("user") User user);
}
