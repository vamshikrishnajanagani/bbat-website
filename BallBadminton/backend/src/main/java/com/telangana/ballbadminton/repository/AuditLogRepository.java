package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.AuditLog.AuditAction;
import com.telangana.ballbadminton.entity.AuditLog.AuditSeverity;
import com.telangana.ballbadminton.entity.AuditLog.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AuditLog entity
 * 
 * Provides query methods for audit log retrieval and analysis
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Find audit logs by user ID
     */
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find audit logs by username
     */
    Page<AuditLog> findByUsername(String username, Pageable pageable);

    /**
     * Find audit logs by action
     */
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    /**
     * Find audit logs by entity type
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    /**
     * Find audit logs by entity type and entity ID
     */
    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId, Pageable pageable);

    /**
     * Find audit logs by severity
     */
    Page<AuditLog> findBySeverity(AuditSeverity severity, Pageable pageable);

    /**
     * Find audit logs by status
     */
    Page<AuditLog> findByStatus(AuditStatus status, Pageable pageable);

    /**
     * Find audit logs within a time range
     */
    Page<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Find audit logs by user and time range
     */
    Page<AuditLog> findByUserIdAndTimestampBetween(UUID userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Find failed operations
     */
    Page<AuditLog> findByStatusAndSeverityIn(AuditStatus status, List<AuditSeverity> severities, Pageable pageable);

    /**
     * Find security-related events
     */
    @Query("SELECT a FROM AuditLog a WHERE a.action IN :securityActions ORDER BY a.timestamp DESC")
    Page<AuditLog> findSecurityEvents(@Param("securityActions") List<AuditAction> securityActions, Pageable pageable);

    /**
     * Find suspicious activities by IP address
     */
    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.status = 'FAILURE' AND a.timestamp > :since")
    List<AuditLog> findFailedAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Find suspicious activities by user
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.status = 'FAILURE' AND a.timestamp > :since")
    List<AuditLog> findFailedAttemptsByUser(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Count operations by user in time range
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :startTime AND :endTime")
    long countByUserInTimeRange(@Param("userId") UUID userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Count failed login attempts by IP
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.action = 'LOGIN_FAILED' AND a.timestamp > :since")
    long countFailedLoginsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Get audit statistics by action
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime GROUP BY a.action")
    List<Object[]> getActionStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Get audit statistics by severity
     */
    @Query("SELECT a.severity, COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime GROUP BY a.severity")
    List<Object[]> getSeverityStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Delete old audit logs (for cleanup)
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);

    /**
     * Find recent critical events
     */
    @Query("SELECT a FROM AuditLog a WHERE a.severity = 'CRITICAL' AND a.timestamp > :since ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentCriticalEvents(@Param("since") LocalDateTime since);

    /**
     * Find correlation ID for tracking related operations
     */
    List<AuditLog> findByCorrelationIdOrderByTimestampAsc(String correlationId);
}
