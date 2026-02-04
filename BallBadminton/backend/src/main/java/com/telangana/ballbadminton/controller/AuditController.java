package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.service.AuditService;
import com.telangana.ballbadminton.service.BackupService;
import com.telangana.ballbadminton.service.SecurityMonitoringService;
import com.telangana.ballbadminton.service.VulnerabilityScanner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for audit logging and monitoring operations
 * 
 * Provides endpoints for:
 * - Viewing audit logs
 * - Security monitoring
 * - Backup management
 * - Vulnerability scanning
 * 
 * Requirements: 6.2, 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/audit")
@Tag(name = "Audit & Monitoring", description = "Audit logging and security monitoring endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;
    private final SecurityMonitoringService securityMonitoringService;
    private final BackupService backupService;
    private final VulnerabilityScanner vulnerabilityScanner;

    public AuditController(AuditService auditService,
                          SecurityMonitoringService securityMonitoringService,
                          BackupService backupService,
                          VulnerabilityScanner vulnerabilityScanner) {
        this.auditService = auditService;
        this.securityMonitoringService = securityMonitoringService;
        this.backupService = backupService;
        this.vulnerabilityScanner = vulnerabilityScanner;
    }

    // Audit Log Endpoints

    @GetMapping("/logs")
    @Operation(summary = "Get audit logs", description = "Retrieve paginated audit logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AuditLog> logs = auditService.getAuditLogsByTimeRange(
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now(),
            pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/user/{userId}")
    @Operation(summary = "Get audit logs by user", description = "Retrieve audit logs for a specific user")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logs = auditService.getAuditLogsByUser(userId, pageable);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs by entity", description = "Retrieve audit logs for a specific entity")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logs = auditService.getAuditLogsByEntity(entityType, entityId, pageable);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/security")
    @Operation(summary = "Get security events", description = "Retrieve security-related audit logs")
    public ResponseEntity<Page<AuditLog>> getSecurityEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logs = auditService.getSecurityEvents(pageable);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/failures")
    @Operation(summary = "Get failed operations", description = "Retrieve failed operation audit logs")
    public ResponseEntity<Page<AuditLog>> getFailedOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logs = auditService.getFailedOperations(pageable);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/statistics")
    @Operation(summary = "Get audit statistics", description = "Retrieve audit log statistics")
    public ResponseEntity<Map<String, Object>> getAuditStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Map<String, Object> statistics = auditService.getAuditStatistics(startTime, endTime);
        return ResponseEntity.ok(statistics);
    }

    // Security Monitoring Endpoints

    @GetMapping("/security/metrics")
    @Operation(summary = "Get security metrics", description = "Retrieve security monitoring metrics")
    public ResponseEntity<Map<String, Object>> getSecurityMetrics() {
        Map<String, Object> metrics = securityMonitoringService.getSecurityMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/security/blocked-ips")
    @Operation(summary = "Get blocked IPs", description = "Retrieve list of blocked IP addresses")
    public ResponseEntity<List<String>> getBlockedIps() {
        return ResponseEntity.ok(List.copyOf(securityMonitoringService.getBlockedIps()));
    }

    @PostMapping("/security/block-ip")
    @Operation(summary = "Block IP address", description = "Manually block an IP address")
    public ResponseEntity<Void> blockIp(
            @RequestParam String ipAddress,
            @RequestParam String reason) {
        
        securityMonitoringService.blockIp(ipAddress, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/security/unblock-ip")
    @Operation(summary = "Unblock IP address", description = "Unblock a previously blocked IP address")
    public ResponseEntity<Void> unblockIp(@RequestParam String ipAddress) {
        securityMonitoringService.unblockIp(ipAddress);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/security/clear-blocked-ips")
    @Operation(summary = "Clear all blocked IPs", description = "Clear all blocked IP addresses")
    public ResponseEntity<Void> clearBlockedIps() {
        securityMonitoringService.clearBlockedIps();
        return ResponseEntity.ok().build();
    }

    // Backup Management Endpoints

    @PostMapping("/backup/database")
    @Operation(summary = "Create database backup", description = "Manually trigger a database backup")
    public ResponseEntity<Map<String, String>> createDatabaseBackup() {
        String backupPath = backupService.createDatabaseBackup();
        return ResponseEntity.ok(Map.of("backupPath", backupPath));
    }

    @PostMapping("/backup/files")
    @Operation(summary = "Create file backup", description = "Manually trigger a file backup")
    public ResponseEntity<Map<String, String>> createFileBackup() {
        String backupPath = backupService.createFileBackup();
        return ResponseEntity.ok(Map.of("backupPath", backupPath));
    }

    @GetMapping("/backup/list")
    @Operation(summary = "List backups", description = "Retrieve list of available backups")
    public ResponseEntity<List<BackupService.BackupInfo>> listBackups() {
        List<BackupService.BackupInfo> backups = backupService.listBackups();
        return ResponseEntity.ok(backups);
    }

    @PostMapping("/backup/restore")
    @Operation(summary = "Restore from backup", description = "Restore database from a backup file")
    public ResponseEntity<Void> restoreBackup(@RequestParam String backupFilePath) {
        backupService.restoreDatabase(backupFilePath);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/backup/verify")
    @Operation(summary = "Verify backup", description = "Verify integrity of a backup file")
    public ResponseEntity<Map<String, Boolean>> verifyBackup(@RequestParam String backupFilePath) {
        boolean isValid = backupService.verifyBackup(backupFilePath);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    // Vulnerability Scanning Endpoints

    @PostMapping("/security/scan")
    @Operation(summary = "Run security scan", description = "Perform a comprehensive security scan")
    public ResponseEntity<VulnerabilityScanner.SecurityScanReport> performSecurityScan() {
        VulnerabilityScanner.SecurityScanReport report = vulnerabilityScanner.performSecurityScan();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/security/updates")
    @Operation(summary = "Check for security updates", description = "Check for available security updates")
    public ResponseEntity<List<VulnerabilityScanner.SecurityUpdate>> checkSecurityUpdates() {
        List<VulnerabilityScanner.SecurityUpdate> updates = vulnerabilityScanner.checkForSecurityUpdates();
        return ResponseEntity.ok(updates);
    }

    // Cleanup Endpoints

    @PostMapping("/cleanup/audit-logs")
    @Operation(summary = "Cleanup old audit logs", description = "Delete audit logs older than specified days")
    public ResponseEntity<Void> cleanupAuditLogs(@RequestParam(defaultValue = "90") int retentionDays) {
        auditService.cleanupOldAuditLogs(retentionDays);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cleanup/backups")
    @Operation(summary = "Cleanup old backups", description = "Delete backups older than retention period")
    public ResponseEntity<Void> cleanupBackups() {
        backupService.cleanupOldBackups();
        return ResponseEntity.ok().build();
    }
}
