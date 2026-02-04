package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.AuditLog.AuditAction;
import com.telangana.ballbadminton.entity.AuditLog.AuditSeverity;
import com.telangana.ballbadminton.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for security monitoring and alerting
 * 
 * This service provides:
 * - Real-time security threat detection
 * - Automated alerting for suspicious activities
 * - Security metrics and reporting
 * - Brute force attack detection
 * - Anomaly detection
 * 
 * Requirements: 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class SecurityMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityMonitoringService.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("com.telangana.ballbadminton.security");

    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;
    private final EmailService emailService;

    // Security thresholds
    private static final int FAILED_LOGIN_THRESHOLD = 5;
    private static final int FAILED_LOGIN_WINDOW_MINUTES = 15;
    private static final int SUSPICIOUS_ACTIVITY_THRESHOLD = 10;
    private static final int RATE_LIMIT_THRESHOLD = 100;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;

    // Blocked IPs cache (in production, use Redis)
    private final Set<String> blockedIps = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Integer> ipFailureCount = Collections.synchronizedMap(new HashMap<>());

    public SecurityMonitoringService(AuditLogRepository auditLogRepository, 
                                    AuditService auditService,
                                    EmailService emailService) {
        this.auditLogRepository = auditLogRepository;
        this.auditService = auditService;
        this.emailService = emailService;
    }

    /**
     * Check if an IP address is blocked
     */
    public boolean isIpBlocked(String ipAddress) {
        return blockedIps.contains(ipAddress);
    }

    /**
     * Block an IP address
     */
    public void blockIp(String ipAddress, String reason) {
        blockedIps.add(ipAddress);
        securityLogger.warn("IP address blocked: {} - Reason: {}", ipAddress, reason);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ipAddress", ipAddress);
        metadata.put("reason", reason);
        metadata.put("timestamp", LocalDateTime.now());
        
        auditService.logSuspiciousActivity("IP address blocked: " + ipAddress, metadata);
        
        // Send alert email
        sendSecurityAlert("IP Address Blocked", 
            String.format("IP address %s has been blocked. Reason: %s", ipAddress, reason));
    }

    /**
     * Unblock an IP address
     */
    public void unblockIp(String ipAddress) {
        blockedIps.remove(ipAddress);
        ipFailureCount.remove(ipAddress);
        securityLogger.info("IP address unblocked: {}", ipAddress);
    }

    /**
     * Monitor failed login attempts
     */
    public void monitorFailedLogin(String ipAddress, String username) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(FAILED_LOGIN_WINDOW_MINUTES);
        
        // Count recent failed attempts from this IP
        long failedAttempts = auditLogRepository.countFailedLoginsByIp(ipAddress, since);
        
        if (failedAttempts >= FAILED_LOGIN_THRESHOLD) {
            // Block the IP
            blockIp(ipAddress, String.format("Excessive failed login attempts (%d in %d minutes)", 
                failedAttempts, FAILED_LOGIN_WINDOW_MINUTES));
            
            // Log critical security event
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ipAddress", ipAddress);
            metadata.put("username", username);
            metadata.put("failedAttempts", failedAttempts);
            metadata.put("timeWindow", FAILED_LOGIN_WINDOW_MINUTES + " minutes");
            
            auditService.logSuspiciousActivity(
                "Brute force attack detected from IP: " + ipAddress, metadata);
        } else if (failedAttempts >= FAILED_LOGIN_THRESHOLD / 2) {
            // Warning threshold reached
            securityLogger.warn("Multiple failed login attempts from IP: {} ({})", 
                ipAddress, failedAttempts);
        }
    }

    /**
     * Monitor rate limiting violations
     */
    public boolean checkRateLimit(UUID userId, String ipAddress) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES);
        
        long requestCount = 0;
        if (userId != null) {
            requestCount = auditLogRepository.countByUserInTimeRange(
                userId, since, LocalDateTime.now());
        }
        
        if (requestCount > RATE_LIMIT_THRESHOLD) {
            securityLogger.warn("Rate limit exceeded for user: {} - {} requests in {} minute(s)", 
                userId, requestCount, RATE_LIMIT_WINDOW_MINUTES);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("userId", userId);
            metadata.put("ipAddress", ipAddress);
            metadata.put("requestCount", requestCount);
            metadata.put("timeWindow", RATE_LIMIT_WINDOW_MINUTES + " minutes");
            
            auditService.logSuspiciousActivity(
                "Rate limit exceeded for user: " + userId, metadata);
            
            return false; // Rate limit exceeded
        }
        
        return true; // Within rate limit
    }

    /**
     * Detect anomalous behavior patterns
     */
    public void detectAnomalies(UUID userId) {
        LocalDateTime since = LocalDateTime.now().minusHours(1);
        
        // Get recent failed operations for user
        List<AuditLog> failedOperations = auditLogRepository.findFailedAttemptsByUser(userId, since);
        
        if (failedOperations.size() > SUSPICIOUS_ACTIVITY_THRESHOLD) {
            securityLogger.warn("Anomalous behavior detected for user: {} - {} failed operations in 1 hour", 
                userId, failedOperations.size());
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("userId", userId);
            metadata.put("failedOperations", failedOperations.size());
            metadata.put("timeWindow", "1 hour");
            
            auditService.logSuspiciousActivity(
                "Anomalous behavior detected for user: " + userId, metadata);
            
            // Send alert
            sendSecurityAlert("Anomalous Behavior Detected", 
                String.format("User %s has %d failed operations in the last hour", 
                    userId, failedOperations.size()));
        }
    }

    /**
     * Get security dashboard metrics
     */
    public Map<String, Object> getSecurityMetrics() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Failed login attempts
        List<AuditLog> failedLogins = auditLogRepository.findByAction(
            AuditAction.LOGIN_FAILED, 
            org.springframework.data.domain.PageRequest.of(0, 1000))
            .getContent()
            .stream()
            .filter(log -> log.getTimestamp().isAfter(last24Hours))
            .collect(Collectors.toList());
        
        metrics.put("failedLoginsLast24Hours", failedLogins.size());
        
        // Critical events
        List<AuditLog> criticalEvents = auditLogRepository.findRecentCriticalEvents(last24Hours);
        metrics.put("criticalEventsLast24Hours", criticalEvents.size());
        
        // Blocked IPs
        metrics.put("blockedIpsCount", blockedIps.size());
        metrics.put("blockedIps", new ArrayList<>(blockedIps));
        
        // Access denied events
        long accessDeniedCount = auditLogRepository.findByAction(
            AuditAction.ACCESS_DENIED,
            org.springframework.data.domain.PageRequest.of(0, 1000))
            .getContent()
            .stream()
            .filter(log -> log.getTimestamp().isAfter(last24Hours))
            .count();
        
        metrics.put("accessDeniedLast24Hours", accessDeniedCount);
        
        // Suspicious activities
        long suspiciousActivities = auditLogRepository.findByAction(
            AuditAction.SUSPICIOUS_ACTIVITY,
            org.springframework.data.domain.PageRequest.of(0, 1000))
            .getContent()
            .stream()
            .filter(log -> log.getTimestamp().isAfter(last24Hours))
            .count();
        
        metrics.put("suspiciousActivitiesLast24Hours", suspiciousActivities);
        
        // Top failed IPs
        Map<String, Long> topFailedIps = failedLogins.stream()
            .filter(log -> log.getIpAddress() != null)
            .collect(Collectors.groupingBy(
                AuditLog::getIpAddress,
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
        
        metrics.put("topFailedIps", topFailedIps);
        
        return metrics;
    }

    /**
     * Scheduled task to monitor security events
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void monitorSecurityEvents() {
        try {
            logger.info("Running scheduled security monitoring check");
            
            LocalDateTime since = LocalDateTime.now().minusMinutes(5);
            
            // Check for critical events
            List<AuditLog> criticalEvents = auditLogRepository.findRecentCriticalEvents(since);
            
            if (!criticalEvents.isEmpty()) {
                securityLogger.warn("Found {} critical security events in the last 5 minutes", 
                    criticalEvents.size());
                
                // Send alert for critical events
                StringBuilder alertMessage = new StringBuilder();
                alertMessage.append("Critical security events detected:\n\n");
                
                for (AuditLog event : criticalEvents) {
                    alertMessage.append(String.format("- %s: %s (User: %s, IP: %s)\n",
                        event.getAction(),
                        event.getDescription(),
                        event.getUsername(),
                        event.getIpAddress()));
                }
                
                sendSecurityAlert("Critical Security Events", alertMessage.toString());
            }
            
            // Clean up old blocked IPs (unblock after 24 hours)
            // In production, this should be more sophisticated
            
        } catch (Exception e) {
            logger.error("Error during security monitoring: {}", e.getMessage(), e);
        }
    }

    /**
     * Scheduled task to generate security reports
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void generateDailySecurityReport() {
        try {
            logger.info("Generating daily security report");
            
            Map<String, Object> metrics = getSecurityMetrics();
            
            StringBuilder report = new StringBuilder();
            report.append("Daily Security Report\n");
            report.append("=====================\n\n");
            report.append(String.format("Failed Logins (24h): %s\n", metrics.get("failedLoginsLast24Hours")));
            report.append(String.format("Critical Events (24h): %s\n", metrics.get("criticalEventsLast24Hours")));
            report.append(String.format("Access Denied (24h): %s\n", metrics.get("accessDeniedLast24Hours")));
            report.append(String.format("Suspicious Activities (24h): %s\n", metrics.get("suspiciousActivitiesLast24Hours")));
            report.append(String.format("Blocked IPs: %s\n", metrics.get("blockedIpsCount")));
            
            @SuppressWarnings("unchecked")
            Map<String, Long> topFailedIps = (Map<String, Long>) metrics.get("topFailedIps");
            if (!topFailedIps.isEmpty()) {
                report.append("\nTop Failed Login IPs:\n");
                topFailedIps.forEach((ip, count) -> 
                    report.append(String.format("  %s: %d attempts\n", ip, count)));
            }
            
            securityLogger.info("Daily security report:\n{}", report);
            
            // Send report via email
            sendSecurityAlert("Daily Security Report", report.toString());
            
        } catch (Exception e) {
            logger.error("Error generating daily security report: {}", e.getMessage(), e);
        }
    }

    /**
     * Send security alert email
     */
    private void sendSecurityAlert(String subject, String message) {
        try {
            // In production, configure admin email addresses
            String adminEmail = "admin@telanganaballbadminton.org";
            emailService.sendEmail(adminEmail, subject, message);
        } catch (Exception e) {
            logger.error("Failed to send security alert email: {}", e.getMessage());
        }
    }

    /**
     * Get list of blocked IPs
     */
    public Set<String> getBlockedIps() {
        return new HashSet<>(blockedIps);
    }

    /**
     * Clear all blocked IPs (admin function)
     */
    public void clearBlockedIps() {
        blockedIps.clear();
        ipFailureCount.clear();
        securityLogger.info("All blocked IPs cleared");
    }
}
