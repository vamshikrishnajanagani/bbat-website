package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.dto.admin.*;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminController
 * 
 * Tests admin endpoints including:
 * - Bulk operations
 * - Content scheduling
 * - Backup and restore
 * - System health monitoring
 * 
 * Requirements: 6.3, 6.4, 6.5
 */
@DisplayName("AdminController Tests")
class AdminControllerTest extends BaseUnitTest {

    @Mock
    private BulkOperationService bulkOperationService;

    @Mock
    private ContentSchedulingService contentSchedulingService;

    @Mock
    private BackupService backupService;

    @Mock
    private SystemHealthService systemHealthService;

    @InjectMocks
    private AdminController adminController;

    private BulkOperationRequest bulkRequest;
    private SchedulePublicationRequest scheduleRequest;

    @BeforeEach
    void setupTest() {
        bulkRequest = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        bulkRequest.setEntityIds(Arrays.asList("article-1", "article-2"));

        scheduleRequest = new SchedulePublicationRequest(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1",
            LocalDateTime.now().plusDays(1)
        );
    }

    // ========== Bulk Operations Tests ==========

    @Test
    @DisplayName("Should execute bulk operation successfully")
    void testExecuteBulkOperation_Success() {
        // Arrange
        BulkOperationResponse mockResponse = new BulkOperationResponse();
        mockResponse.setOperation(BulkOperationRequest.OperationType.DELETE);
        mockResponse.setEntityType(BulkOperationRequest.EntityType.NEWS_ARTICLE);
        mockResponse.setTotalCount(2);
        mockResponse.setSuccessCount(2);
        mockResponse.setFailureCount(0);

        when(bulkOperationService.executeBulkOperation(any(BulkOperationRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<BulkOperationResponse> response = adminController.executeBulkOperation(bulkRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccessCount()).isEqualTo(2);
        assertThat(response.getBody().getFailureCount()).isEqualTo(0);

        verify(bulkOperationService).executeBulkOperation(bulkRequest);
    }

    @Test
    @DisplayName("Should handle bulk operation failure")
    void testExecuteBulkOperation_Failure() {
        // Arrange
        when(bulkOperationService.executeBulkOperation(any(BulkOperationRequest.class)))
            .thenThrow(new RuntimeException("Bulk operation failed"));

        // Act
        ResponseEntity<BulkOperationResponse> response = adminController.executeBulkOperation(bulkRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResults()).isNotEmpty();
        assertThat(response.getBody().getResults().get(0).getMessage()).contains("Bulk operation failed");
    }

    // ========== Content Scheduling Tests ==========

    @Test
    @DisplayName("Should schedule publication successfully")
    void testSchedulePublication_Success() {
        // Arrange
        when(contentSchedulingService.schedulePublication(any(SchedulePublicationRequest.class)))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.schedulePublication(scheduleRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(true);
        assertThat(response.getBody().get("message")).isEqualTo("Publication scheduled successfully");

        verify(contentSchedulingService).schedulePublication(scheduleRequest);
    }

    @Test
    @DisplayName("Should return 404 when scheduling publication for non-existent entity")
    void testSchedulePublication_EntityNotFound() {
        // Arrange
        when(contentSchedulingService.schedulePublication(any(SchedulePublicationRequest.class)))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.schedulePublication(scheduleRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(false);
    }

    @Test
    @DisplayName("Should cancel scheduled publication successfully")
    void testCancelScheduledPublication_Success() {
        // Arrange
        when(contentSchedulingService.cancelScheduledPublication(
            any(BulkOperationRequest.EntityType.class), anyString()))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.cancelScheduledPublication(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1"
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).contains("cancelled successfully");
    }

    @Test
    @DisplayName("Should return 404 when cancelling non-existent scheduled publication")
    void testCancelScheduledPublication_NotFound() {
        // Arrange
        when(contentSchedulingService.cancelScheduledPublication(
            any(BulkOperationRequest.EntityType.class), anyString()))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.cancelScheduledPublication(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1"
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all scheduled publications")
    void testGetScheduledPublications() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setId("article-1");
        article1.setScheduledPublicationDate(LocalDateTime.now().plusDays(1));

        NewsArticle article2 = new NewsArticle();
        article2.setId("article-2");
        article2.setScheduledPublicationDate(LocalDateTime.now().plusDays(2));

        List<NewsArticle> scheduledArticles = Arrays.asList(article1, article2);

        when(contentSchedulingService.getScheduledPublications()).thenReturn(scheduledArticles);
        when(contentSchedulingService.getPendingScheduledCount()).thenReturn(2L);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.getScheduledPublications(null, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("count")).isEqualTo(2);
        assertThat(response.getBody().get("pendingCount")).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should get scheduled publications by date range")
    void testGetScheduledPublications_WithDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        NewsArticle article = new NewsArticle();
        article.setId("article-1");

        when(contentSchedulingService.getScheduledPublicationsByDateRange(startDate, endDate))
            .thenReturn(Arrays.asList(article));
        when(contentSchedulingService.getPendingScheduledCount()).thenReturn(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.getScheduledPublications(startDate, endDate);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("count")).isEqualTo(1);

        verify(contentSchedulingService).getScheduledPublicationsByDateRange(startDate, endDate);
    }

    // ========== Backup and Restore Tests ==========

    @Test
    @DisplayName("Should create database backup successfully")
    void testCreateDatabaseBackup_Success() {
        // Arrange
        String backupPath = "/backups/db_backup_20240115_120000.sql";
        when(backupService.createDatabaseBackup()).thenReturn(backupPath);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.createDatabaseBackup();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).contains("successfully");
        assertThat(response.getBody().get("backupPath")).isEqualTo(backupPath);

        verify(backupService).createDatabaseBackup();
    }

    @Test
    @DisplayName("Should handle database backup failure")
    void testCreateDatabaseBackup_Failure() {
        // Arrange
        when(backupService.createDatabaseBackup())
            .thenThrow(new RuntimeException("Backup failed"));

        // Act
        ResponseEntity<Map<String, String>> response = adminController.createDatabaseBackup();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).contains("failed");
    }

    @Test
    @DisplayName("Should create file backup successfully")
    void testCreateFileBackup_Success() {
        // Arrange
        String backupPath = "/backups/files_backup_20240115_120000.tar.gz";
        when(backupService.createFileBackup()).thenReturn(backupPath);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.createFileBackup();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("backupPath")).isEqualTo(backupPath);

        verify(backupService).createFileBackup();
    }

    @Test
    @DisplayName("Should restore database successfully")
    void testRestoreDatabase_Success() {
        // Arrange
        String backupPath = "/backups/db_backup_20240115_120000.sql";
        doNothing().when(backupService).restoreDatabase(backupPath);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.restoreDatabase(backupPath);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).contains("restored successfully");

        verify(backupService).restoreDatabase(backupPath);
    }

    @Test
    @DisplayName("Should handle database restore failure")
    void testRestoreDatabase_Failure() {
        // Arrange
        String backupPath = "/backups/db_backup_20240115_120000.sql";
        doThrow(new RuntimeException("Restore failed"))
            .when(backupService).restoreDatabase(backupPath);

        // Act
        ResponseEntity<Map<String, String>> response = adminController.restoreDatabase(backupPath);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).contains("failed");
    }

    @Test
    @DisplayName("Should list available backups")
    void testListBackups() {
        // Arrange
        BackupService.BackupInfo backup1 = new BackupService.BackupInfo();
        backup1.setFileName("db_backup_20240115_120000.sql");
        backup1.setType("Database");

        BackupService.BackupInfo backup2 = new BackupService.BackupInfo();
        backup2.setFileName("files_backup_20240115_120000.tar.gz");
        backup2.setType("Files");

        List<BackupService.BackupInfo> backups = Arrays.asList(backup1, backup2);

        when(backupService.listBackups()).thenReturn(backups);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.listBackups();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("count")).isEqualTo(2);
        assertThat(response.getBody().get("backups")).isEqualTo(backups);
    }

    @Test
    @DisplayName("Should verify backup integrity")
    void testVerifyBackup_Valid() {
        // Arrange
        String backupPath = "/backups/db_backup_20240115_120000.sql";
        when(backupService.verifyBackup(backupPath)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.verifyBackup(backupPath);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("valid")).isEqualTo(true);
        assertThat(response.getBody().get("message")).isEqualTo("Backup file is valid");
    }

    @Test
    @DisplayName("Should detect invalid backup")
    void testVerifyBackup_Invalid() {
        // Arrange
        String backupPath = "/backups/corrupted_backup.sql";
        when(backupService.verifyBackup(backupPath)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.verifyBackup(backupPath);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("valid")).isEqualTo(false);
        assertThat(response.getBody().get("message")).contains("invalid or corrupted");
    }

    // ========== System Health Tests ==========

    @Test
    @DisplayName("Should get system health with UP status")
    void testGetSystemHealth_Up() {
        // Arrange
        SystemHealthResponse healthResponse = new SystemHealthResponse();
        healthResponse.setStatus("UP");

        when(systemHealthService.getSystemHealth()).thenReturn(healthResponse);

        // Act
        ResponseEntity<SystemHealthResponse> response = adminController.getSystemHealth();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("UP");

        verify(systemHealthService).getSystemHealth();
    }

    @Test
    @DisplayName("Should get system health with DOWN status")
    void testGetSystemHealth_Down() {
        // Arrange
        SystemHealthResponse healthResponse = new SystemHealthResponse();
        healthResponse.setStatus("DOWN");

        when(systemHealthService.getSystemHealth()).thenReturn(healthResponse);

        // Act
        ResponseEntity<SystemHealthResponse> response = adminController.getSystemHealth();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Should get system health with WARNING status")
    void testGetSystemHealth_Warning() {
        // Arrange
        SystemHealthResponse healthResponse = new SystemHealthResponse();
        healthResponse.setStatus("WARNING");

        when(systemHealthService.getSystemHealth()).thenReturn(healthResponse);

        // Act
        ResponseEntity<SystemHealthResponse> response = adminController.getSystemHealth();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("WARNING");
    }

    @Test
    @DisplayName("Should get quick health status - UP")
    void testGetQuickHealthStatus_Up() {
        // Arrange
        when(systemHealthService.getQuickHealthStatus()).thenReturn("UP");

        // Act
        ResponseEntity<Map<String, String>> response = adminController.getQuickHealthStatus();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    @DisplayName("Should get quick health status - DOWN")
    void testGetQuickHealthStatus_Down() {
        // Arrange
        when(systemHealthService.getQuickHealthStatus()).thenReturn("DOWN");

        // Act
        ResponseEntity<Map<String, String>> response = adminController.getQuickHealthStatus();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("DOWN");
    }
}
