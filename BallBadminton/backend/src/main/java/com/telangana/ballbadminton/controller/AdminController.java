package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.admin.*;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for advanced admin features
 * 
 * Provides endpoints for:
 * - Bulk operations on content entities
 * - Content scheduling and publication workflows
 * - Backup and restore functionality
 * - System health monitoring
 * 
 * Requirements: 6.3, 6.4, 6.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Advanced administration endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final BulkOperationService bulkOperationService;
    private final ContentSchedulingService contentSchedulingService;
    private final BackupService backupService;
    private final SystemHealthService systemHealthService;

    public AdminController(
            BulkOperationService bulkOperationService,
            ContentSchedulingService contentSchedulingService,
            BackupService backupService,
            SystemHealthService systemHealthService) {
        this.bulkOperationService = bulkOperationService;
        this.contentSchedulingService = contentSchedulingService;
        this.backupService = backupService;
        this.systemHealthService = systemHealthService;
    }

    // ========== Bulk Operations ==========

    @PostMapping("/bulk-operations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Execute bulk operation",
        description = "Execute bulk create, update, delete, publish, or unpublish operations on content entities. " +
                     "All operations are atomic (all-or-nothing). Requirements: 6.3, 9.5"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bulk operation completed successfully",
            content = @Content(schema = @Schema(implementation = BulkOperationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Bulk operation failed - transaction rolled back")
    })
    public ResponseEntity<BulkOperationResponse> executeBulkOperation(
            @Valid @RequestBody BulkOperationRequest request) {
        
        logger.info("Executing bulk {} operation on {} entities", 
            request.getOperation(), request.getEntityType());

        try {
            BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Bulk operation failed: {}", e.getMessage(), e);
            
            // Return error response
            BulkOperationResponse errorResponse = new BulkOperationResponse();
            errorResponse.setOperation(request.getOperation());
            errorResponse.setEntityType(request.getEntityType());
            errorResponse.setTotalCount(request.getEntityIds() != null ? request.getEntityIds().size() : 0);
            errorResponse.addResult(new BulkOperationResponse.OperationResult(
                null, false, "Bulk operation failed: " + e.getMessage()));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ========== Content Scheduling ==========

    @PostMapping("/schedule-publication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Schedule content for publication",
        description = "Schedule content to be automatically published at a future date and time. " +
                     "Requirements: 6.4"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Publication scheduled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<Map<String, Object>> schedulePublication(
            @Valid @RequestBody SchedulePublicationRequest request) {
        
        logger.info("Scheduling publication for {} with ID {} at {}", 
            request.getEntityType(), request.getEntityId(), request.getScheduledPublicationDate());

        boolean success = contentSchedulingService.schedulePublication(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("entityType", request.getEntityType());
        response.put("entityId", request.getEntityId());
        response.put("scheduledPublicationDate", request.getScheduledPublicationDate());
        response.put("message", success ? 
            "Publication scheduled successfully" : 
            "Failed to schedule publication - entity not found");

        return success ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/schedule-publication/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cancel scheduled publication",
        description = "Cancel a previously scheduled publication. Requirements: 6.4"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Scheduled publication cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<Map<String, String>> cancelScheduledPublication(
            @Parameter(description = "Entity type") 
            @PathVariable BulkOperationRequest.EntityType entityType,
            @Parameter(description = "Entity ID") 
            @PathVariable String entityId) {
        
        logger.info("Cancelling scheduled publication for {} with ID {}", entityType, entityId);

        boolean success = contentSchedulingService.cancelScheduledPublication(entityType, entityId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", success ? 
            "Scheduled publication cancelled successfully" : 
            "Failed to cancel - entity not found or not scheduled");

        return success ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/scheduled-publications")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all scheduled publications",
        description = "Retrieve all content scheduled for future publication. Requirements: 6.4"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Scheduled publications retrieved successfully"
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Map<String, Object>> getScheduledPublications(
            @Parameter(description = "Start date for filtering (optional)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime startDate,
            @Parameter(description = "End date for filtering (optional)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime endDate) {
        
        logger.info("Retrieving scheduled publications");

        List<NewsArticle> scheduledArticles;
        
        if (startDate != null && endDate != null) {
            scheduledArticles = contentSchedulingService
                .getScheduledPublicationsByDateRange(startDate, endDate);
        } else {
            scheduledArticles = contentSchedulingService.getScheduledPublications();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", scheduledArticles.size());
        response.put("scheduledPublications", scheduledArticles);
        response.put("pendingCount", contentSchedulingService.getPendingScheduledCount());

        return ResponseEntity.ok(response);
    }

    // ========== Backup and Restore ==========

    @PostMapping("/backup/database")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create database backup",
        description = "Create a full backup of the database. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Database backup created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Backup failed")
    })
    public ResponseEntity<Map<String, String>> createDatabaseBackup() {
        logger.info("Creating database backup");

        try {
            String backupPath = backupService.createDatabaseBackup();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Database backup created successfully");
            response.put("backupPath", backupPath);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database backup failed: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Database backup failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/backup/files")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create file backup",
        description = "Create a backup of uploaded files. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File backup created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Backup failed")
    })
    public ResponseEntity<Map<String, String>> createFileBackup() {
        logger.info("Creating file backup");

        try {
            String backupPath = backupService.createFileBackup();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File backup created successfully");
            response.put("backupPath", backupPath);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("File backup failed: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "File backup failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/restore/database")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Restore database from backup",
        description = "Restore the database from a backup file. WARNING: This will overwrite current data. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Database restored successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid backup file path"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Restore failed")
    })
    public ResponseEntity<Map<String, String>> restoreDatabase(
            @Parameter(description = "Path to backup file") 
            @RequestParam String backupFilePath) {
        
        logger.warn("Restoring database from backup: {}", backupFilePath);

        try {
            backupService.restoreDatabase(backupFilePath);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Database restored successfully");
            response.put("backupPath", backupFilePath);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database restore failed: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Database restore failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/backups")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List available backups",
        description = "Get a list of all available backup files. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Backup list retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Map<String, Object>> listBackups() {
        logger.info("Listing available backups");

        List<BackupService.BackupInfo> backups = backupService.listBackups();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", backups.size());
        response.put("backups", backups);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/backups/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Verify backup integrity",
        description = "Verify the integrity of a backup file. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Backup verification completed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Map<String, Object>> verifyBackup(
            @Parameter(description = "Path to backup file") 
            @RequestParam String backupFilePath) {
        
        logger.info("Verifying backup: {}", backupFilePath);

        boolean isValid = backupService.verifyBackup(backupFilePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("backupPath", backupFilePath);
        response.put("message", isValid ? 
            "Backup file is valid" : 
            "Backup file is invalid or corrupted");

        return ResponseEntity.ok(response);
    }

    // ========== System Health Monitoring ==========

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get system health status",
        description = "Get comprehensive system health information including database, cache, storage, and service status. Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "System health information retrieved successfully",
            content = @Content(schema = @Schema(implementation = SystemHealthResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<SystemHealthResponse> getSystemHealth() {
        logger.info("Retrieving system health information");

        SystemHealthResponse health = systemHealthService.getSystemHealth();
        
        // Return appropriate HTTP status based on system health
        HttpStatus status = switch (health.getStatus()) {
            case "UP" -> HttpStatus.OK;
            case "WARNING" -> HttpStatus.OK;
            case "DOWN", "ERROR" -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.OK;
        };

        return ResponseEntity.status(status).body(health);
    }

    @GetMapping("/health/quick")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get quick health status",
        description = "Get a lightweight health check (database connectivity only). Requirements: 6.5"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System is healthy"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "503", description = "System is unhealthy")
    })
    public ResponseEntity<Map<String, String>> getQuickHealthStatus() {
        String status = systemHealthService.getQuickHealthStatus();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("timestamp", LocalDateTime.now().toString());

        HttpStatus httpStatus = "UP".equals(status) ? 
            HttpStatus.OK : 
            HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(httpStatus).body(response);
    }
}