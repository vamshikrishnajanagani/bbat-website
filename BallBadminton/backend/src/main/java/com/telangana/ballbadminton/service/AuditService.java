package com.telangana.ballbadminton.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.AuditLog.AuditAction;
import com.telangana.ballbadminton.entity.AuditLog.AuditSeverity;
import com.telangana.ballbadminton.entity.AuditLog.AuditStatus;
import com.telangana.ballbadminton.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for comprehensive audit logging
 * 
 * This service provides:
 * - Automatic audit trail creation for all operations
 * - Security event logging
 * - Performance monitoring
 * - Audit log querying and analysis
 * 
 * Requirements: 6.2, 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("com.telangana.ballbadminton.security");

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Log an audit event asynchronously
     * Uses a separate transaction to ensure audit logs are saved even if main transaction fails
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(AuditLog auditLog) {
        try {
            // Enrich audit log with request context if available
            enrichAuditLogWithContext(auditLog);
            
            // Save to database
            auditLogRepository.save(auditLog);
            
            // Log to security logger for critical events
            if (auditLog.getSeverity() == AuditSeverity.CRITICAL || 
                auditLog.getSeverity() == AuditSeverity.ERROR) {
                securityLogger.warn("Audit Event: {} - {} - {} - {}", 
                    auditLog.getAction(), 
                    auditLog.getSeverity(), 
                    auditLog.getDescription(),
                    auditLog.getUsername());
            }
        } catch (Exception e) {
            // Fallback to file logging if database save fails
            logger.error("Failed to save audit log to database: {}", e.getMessage());
            logger.info("Audit Event (fallback): {} - {} - {} - {}", 
                auditLog.getAction(), 
                auditLog.getSeverity(), 
                auditLog.getDescription(),
                auditLog.getUsername());
        }
    }

    /**
     * Create and log an audit event
     */
    public void audit(AuditAction action, String entityType, String entityId, String description) {
        AuditLog auditLog = createAuditLog(action, entityType, entityId, description);
        logAudit(auditLog);
    }

    /**
     * Create and log an audit event with old and new values
     */
    public void audit(AuditAction action, String entityType, String entityId, String description, 
                     Object oldValues, Object newValues) {
        AuditLog auditLog = createAuditLog(action, entityType, entityId, description);
        
        try {
            if (oldValues != null) {
                auditLog.setOldValues(objectMapper.writeValueAsString(oldValues));
            }
            if (newValues != null) {
                auditLog.setNewValues(objectMapper.writeValueAsString(newValues));
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize audit values: {}", e.getMessage());
        }
        
        logAudit(auditLog);
    }

    /**
     * Log a security event
     */
    public void logSecurityEvent(AuditAction action, String description, AuditSeverity severity) {
        AuditLog auditLog = createAuditLog(action, "Security", null, description);
        auditLog.setSeverity(severity);
        logAudit(auditLog);
    }

    /**
     * Log a failed operation
     */
    public void logFailure(AuditAction action, String entityType, String entityId, 
                          String description, Exception exception) {
        AuditLog auditLog = createAuditLog(action, entityType, entityId, description);
        auditLog.setStatus(AuditStatus.FAILURE);
        auditLog.setSeverity(AuditSeverity.ERROR);
        auditLog.setErrorMessage(exception.getMessage());
        auditLog.setStackTrace(getStackTrace(exception));
        logAudit(auditLog);
    }

    /**
     * Log a login attempt
     */
    public void logLogin(String username, boolean success, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(success ? AuditAction.LOGIN : AuditAction.LOGIN_FAILED);
        auditLog.setUsername(username);
        auditLog.setEntityType("Authentication");
        auditLog.setDescription(success ? "User logged in successfully" : "Login attempt failed");
        auditLog.setStatus(success ? AuditStatus.SUCCESS : AuditStatus.FAILURE);
        auditLog.setSeverity(success ? AuditSeverity.INFO : AuditSeverity.WARNING);
        auditLog.setIpAddress(ipAddress);
        logAudit(auditLog);
    }

    /**
     * Log a logout
     */
    public void logLogout(String username) {
        audit(AuditAction.LOGOUT, "Authentication", null, "User logged out");
    }

    /**
     * Log an access denied event
     */
    public void logAccessDenied(String resource, String reason) {
        AuditLog auditLog = createAuditLog(AuditAction.ACCESS_DENIED, "Security", null, 
            "Access denied to resource: " + resource + ". Reason: " + reason);
        auditLog.setSeverity(AuditSeverity.WARNING);
        auditLog.setStatus(AuditStatus.FAILURE);
        logAudit(auditLog);
    }

    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(String description, Map<String, Object> metadata) {
        AuditLog auditLog = createAuditLog(AuditAction.SUSPICIOUS_ACTIVITY, "Security", null, description);
        auditLog.setSeverity(AuditSeverity.CRITICAL);
        
        try {
            auditLog.setMetadata(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize metadata: {}", e.getMessage());
        }
        
        logAudit(auditLog);
    }

    /**
     * Create a basic audit log with common fields populated
     */
    private AuditLog createAuditLog(AuditAction action, String entityType, String entityId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        
        // Get current user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            auditLog.setUsername(authentication.getName());
            // Note: userId extraction depends on your UserDetails implementation
            // This is a placeholder - adjust based on your actual implementation
        }
        
        return auditLog;
    }

    /**
     * Enrich audit log with HTTP request context
     */
    private void enrichAuditLogWithContext(AuditLog auditLog) {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                if (auditLog.getIpAddress() == null) {
                    auditLog.setIpAddress(getClientIpAddress(request));
                }
                
                if (auditLog.getUserAgent() == null) {
                    auditLog.setUserAgent(request.getHeader("User-Agent"));
                }
                
                if (auditLog.getRequestMethod() == null) {
                    auditLog.setRequestMethod(request.getMethod());
                }
                
                if (auditLog.getRequestUrl() == null) {
                    auditLog.setRequestUrl(request.getRequestURI());
                }
                
                // Get session ID if available
                if (auditLog.getSessionId() == null && request.getSession(false) != null) {
                    auditLog.setSessionId(request.getSession(false).getId());
                }
            }
        } catch (Exception e) {
            logger.debug("Could not enrich audit log with request context: {}", e.getMessage());
        }
    }

    /**
     * Get client IP address from request, handling proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs (take the first one)
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        
        // Limit stack trace length to avoid database issues
        if (stackTrace.length() > 5000) {
            stackTrace = stackTrace.substring(0, 5000) + "... (truncated)";
        }
        
        return stackTrace;
    }

    // Query methods for audit log retrieval

    /**
     * Get audit logs by user
     */
    public Page<AuditLog> getAuditLogsByUser(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Get audit logs by entity
     */
    public Page<AuditLog> getAuditLogsByEntity(String entityType, String entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    /**
     * Get audit logs by time range
     */
    public Page<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(startTime, endTime, pageable);
    }

    /**
     * Get security events
     */
    public Page<AuditLog> getSecurityEvents(Pageable pageable) {
        List<AuditAction> securityActions = List.of(
            AuditAction.LOGIN,
            AuditAction.LOGOUT,
            AuditAction.LOGIN_FAILED,
            AuditAction.ACCESS_DENIED,
            AuditAction.PERMISSION_DENIED,
            AuditAction.UNAUTHORIZED_ACCESS,
            AuditAction.SUSPICIOUS_ACTIVITY
        );
        return auditLogRepository.findSecurityEvents(securityActions, pageable);
    }

    /**
     * Get failed operations
     */
    public Page<AuditLog> getFailedOperations(Pageable pageable) {
        List<AuditSeverity> severities = List.of(AuditSeverity.ERROR, AuditSeverity.CRITICAL);
        return auditLogRepository.findByStatusAndSeverityIn(AuditStatus.FAILURE, severities, pageable);
    }

    /**
     * Get audit statistics
     */
    public Map<String, Object> getAuditStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statistics = new HashMap<>();
        
        List<Object[]> actionStats = auditLogRepository.getActionStatistics(startTime, endTime);
        Map<String, Long> actionCounts = new HashMap<>();
        for (Object[] stat : actionStats) {
            actionCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        statistics.put("actionStatistics", actionCounts);
        
        List<Object[]> severityStats = auditLogRepository.getSeverityStatistics(startTime, endTime);
        Map<String, Long> severityCounts = new HashMap<>();
        for (Object[] stat : severityStats) {
            severityCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        statistics.put("severityStatistics", severityCounts);
        
        return statistics;
    }

    /**
     * Check for suspicious activity patterns
     */
    public boolean detectSuspiciousActivity(UUID userId, String ipAddress) {
        LocalDateTime since = LocalDateTime.now().minusHours(1);
        
        // Check for excessive failed attempts
        long failedAttempts = 0;
        if (userId != null) {
            failedAttempts = auditLogRepository.findFailedAttemptsByUser(userId, since).size();
        } else if (ipAddress != null) {
            failedAttempts = auditLogRepository.countFailedLoginsByIp(ipAddress, since);
        }
        
        // Threshold: more than 5 failed attempts in 1 hour
        return failedAttempts > 5;
    }

    /**
     * Cleanup old audit logs (should be run as scheduled task)
     */
    @Transactional
    public void cleanupOldAuditLogs(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        auditLogRepository.deleteByTimestampBefore(cutoffDate);
        logger.info("Cleaned up audit logs older than {} days", retentionDays);
    }
}
