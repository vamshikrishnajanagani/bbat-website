package com.telangana.ballbadminton.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for backup and disaster recovery procedures
 * 
 * This service provides:
 * - Automated database backups
 * - Backup retention management
 * - Backup verification
 * - Disaster recovery procedures
 * - File system backups
 * 
 * Requirements: 6.5, 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${app.backup.directory:./backups}")
    private String backupDirectory;

    @Value("${app.backup.retention-days:30}")
    private int retentionDays;

    @Value("${spring.datasource.url:}")
    private String databaseUrl;

    @Value("${spring.datasource.username:}")
    private String databaseUsername;

    @Value("${spring.datasource.password:}")
    private String databasePassword;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDirectory;

    private final AuditService auditService;
    private final EmailService emailService;

    public BackupService(AuditService auditService, EmailService emailService) {
        this.auditService = auditService;
        this.emailService = emailService;
    }

    /**
     * Create a full database backup
     */
    public String createDatabaseBackup() {
        try {
            logger.info("Starting database backup");
            
            // Create backup directory if it doesn't exist
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            
            // Generate backup filename with timestamp
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String backupFileName = String.format("db_backup_%s.sql", timestamp);
            String backupFilePath = Paths.get(backupDirectory, backupFileName).toString();
            
            // Extract database name from JDBC URL
            String dbName = extractDatabaseName(databaseUrl);
            
            // Execute pg_dump command (for PostgreSQL)
            // Note: This assumes PostgreSQL. Adjust for other databases.
            ProcessBuilder processBuilder = new ProcessBuilder(
                "pg_dump",
                "-h", extractHost(databaseUrl),
                "-p", extractPort(databaseUrl),
                "-U", databaseUsername,
                "-F", "c", // Custom format (compressed)
                "-b", // Include large objects
                "-v", // Verbose
                "-f", backupFilePath,
                dbName
            );
            
            // Set password via environment variable
            processBuilder.environment().put("PGPASSWORD", databasePassword);
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Database backup completed successfully: {}", backupFilePath);
                
                // Verify backup file exists and has content
                File backupFile = new File(backupFilePath);
                if (backupFile.exists() && backupFile.length() > 0) {
                    auditService.audit(
                        com.telangana.ballbadminton.entity.AuditLog.AuditAction.BACKUP,
                        "Database",
                        dbName,
                        "Database backup created successfully: " + backupFileName
                    );
                    
                    return backupFilePath;
                } else {
                    throw new RuntimeException("Backup file is empty or does not exist");
                }
            } else {
                // Read error output
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
                
                throw new RuntimeException("Database backup failed with exit code " + exitCode + 
                    ": " + errorOutput);
            }
            
        } catch (Exception e) {
            logger.error("Database backup failed: {}", e.getMessage(), e);
            
            auditService.logFailure(
                com.telangana.ballbadminton.entity.AuditLog.AuditAction.BACKUP,
                "Database",
                null,
                "Database backup failed",
                e
            );
            
            // Send alert email
            sendBackupAlert("Database Backup Failed", 
                "Database backup failed: " + e.getMessage());
            
            throw new RuntimeException("Database backup failed", e);
        }
    }

    /**
     * Create a backup of uploaded files
     */
    public String createFileBackup() {
        try {
            logger.info("Starting file backup");
            
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String backupFileName = String.format("files_backup_%s.tar.gz", timestamp);
            String backupFilePath = Paths.get(backupDirectory, backupFileName).toString();
            
            // Create tar.gz archive of upload directory
            ProcessBuilder processBuilder = new ProcessBuilder(
                "tar",
                "-czf",
                backupFilePath,
                "-C",
                new File(uploadDirectory).getParent(),
                new File(uploadDirectory).getName()
            );
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("File backup completed successfully: {}", backupFilePath);
                
                auditService.audit(
                    com.telangana.ballbadminton.entity.AuditLog.AuditAction.BACKUP,
                    "Files",
                    uploadDirectory,
                    "File backup created successfully: " + backupFileName
                );
                
                return backupFilePath;
            } else {
                throw new RuntimeException("File backup failed with exit code " + exitCode);
            }
            
        } catch (Exception e) {
            logger.error("File backup failed: {}", e.getMessage(), e);
            
            auditService.logFailure(
                com.telangana.ballbadminton.entity.AuditLog.AuditAction.BACKUP,
                "Files",
                uploadDirectory,
                "File backup failed",
                e
            );
            
            throw new RuntimeException("File backup failed", e);
        }
    }

    /**
     * Restore database from backup
     */
    public void restoreDatabase(String backupFilePath) {
        try {
            logger.info("Starting database restore from: {}", backupFilePath);
            
            File backupFile = new File(backupFilePath);
            if (!backupFile.exists()) {
                throw new RuntimeException("Backup file not found: " + backupFilePath);
            }
            
            String dbName = extractDatabaseName(databaseUrl);
            
            // Execute pg_restore command (for PostgreSQL)
            ProcessBuilder processBuilder = new ProcessBuilder(
                "pg_restore",
                "-h", extractHost(databaseUrl),
                "-p", extractPort(databaseUrl),
                "-U", databaseUsername,
                "-d", dbName,
                "-c", // Clean (drop) database objects before recreating
                "-v", // Verbose
                backupFilePath
            );
            
            processBuilder.environment().put("PGPASSWORD", databasePassword);
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Database restore completed successfully");
                
                auditService.audit(
                    com.telangana.ballbadminton.entity.AuditLog.AuditAction.RESTORE,
                    "Database",
                    dbName,
                    "Database restored from backup: " + backupFilePath
                );
            } else {
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
                
                throw new RuntimeException("Database restore failed with exit code " + exitCode + 
                    ": " + errorOutput);
            }
            
        } catch (Exception e) {
            logger.error("Database restore failed: {}", e.getMessage(), e);
            
            auditService.logFailure(
                com.telangana.ballbadminton.entity.AuditLog.AuditAction.RESTORE,
                "Database",
                null,
                "Database restore failed",
                e
            );
            
            throw new RuntimeException("Database restore failed", e);
        }
    }

    /**
     * List available backups
     */
    public List<BackupInfo> listBackups() {
        try {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                return new ArrayList<>();
            }
            
            try (Stream<Path> paths = Files.list(backupPath)) {
                return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".sql") || 
                                   path.getFileName().toString().endsWith(".tar.gz"))
                    .map(this::createBackupInfo)
                    .sorted(Comparator.comparing(BackupInfo::getTimestamp).reversed())
                    .collect(Collectors.toList());
            }
            
        } catch (IOException e) {
            logger.error("Failed to list backups: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Clean up old backups based on retention policy
     */
    @Scheduled(cron = "0 0 3 * * *") // Run daily at 3 AM
    public void cleanupOldBackups() {
        try {
            logger.info("Starting backup cleanup (retention: {} days)", retentionDays);
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            
            List<BackupInfo> backups = listBackups();
            int deletedCount = 0;
            
            for (BackupInfo backup : backups) {
                if (backup.getTimestamp().isBefore(cutoffDate)) {
                    try {
                        Files.delete(Paths.get(backup.getFilePath()));
                        deletedCount++;
                        logger.info("Deleted old backup: {}", backup.getFileName());
                    } catch (IOException e) {
                        logger.error("Failed to delete backup {}: {}", 
                            backup.getFileName(), e.getMessage());
                    }
                }
            }
            
            logger.info("Backup cleanup completed. Deleted {} old backups", deletedCount);
            
            if (deletedCount > 0) {
                auditService.audit(
                    com.telangana.ballbadminton.entity.AuditLog.AuditAction.SYSTEM_WARNING,
                    "Backup",
                    null,
                    String.format("Cleaned up %d old backups (retention: %d days)", 
                        deletedCount, retentionDays)
                );
            }
            
        } catch (Exception e) {
            logger.error("Backup cleanup failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Scheduled database backup - runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledDatabaseBackup() {
        try {
            logger.info("Starting scheduled database backup");
            String backupPath = createDatabaseBackup();
            logger.info("Scheduled database backup completed: {}", backupPath);
            
            // Send success notification
            sendBackupAlert("Database Backup Successful", 
                "Scheduled database backup completed successfully: " + backupPath);
            
        } catch (Exception e) {
            logger.error("Scheduled database backup failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Scheduled file backup - runs daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledFileBackup() {
        try {
            logger.info("Starting scheduled file backup");
            String backupPath = createFileBackup();
            logger.info("Scheduled file backup completed: {}", backupPath);
            
        } catch (Exception e) {
            logger.error("Scheduled file backup failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Verify backup integrity
     */
    public boolean verifyBackup(String backupFilePath) {
        try {
            File backupFile = new File(backupFilePath);
            
            if (!backupFile.exists()) {
                logger.error("Backup file not found: {}", backupFilePath);
                return false;
            }
            
            if (backupFile.length() == 0) {
                logger.error("Backup file is empty: {}", backupFilePath);
                return false;
            }
            
            // Additional verification could include:
            // - Checksum verification
            // - Test restore to temporary database
            // - Content validation
            
            logger.info("Backup verification passed: {}", backupFilePath);
            return true;
            
        } catch (Exception e) {
            logger.error("Backup verification failed: {}", e.getMessage(), e);
            return false;
        }
    }

    // Helper methods

    private BackupInfo createBackupInfo(Path path) {
        try {
            BackupInfo info = new BackupInfo();
            info.setFileName(path.getFileName().toString());
            info.setFilePath(path.toString());
            info.setSize(Files.size(path));
            info.setTimestamp(LocalDateTime.ofInstant(
                Files.getLastModifiedTime(path).toInstant(),
                java.time.ZoneId.systemDefault()
            ));
            
            if (path.getFileName().toString().startsWith("db_backup_")) {
                info.setType("Database");
            } else if (path.getFileName().toString().startsWith("files_backup_")) {
                info.setType("Files");
            } else {
                info.setType("Unknown");
            }
            
            return info;
        } catch (IOException e) {
            logger.error("Failed to create backup info: {}", e.getMessage());
            return null;
        }
    }

    private String extractDatabaseName(String jdbcUrl) {
        // Extract database name from JDBC URL
        // Example: jdbc:postgresql://localhost:5432/dbname
        String[] parts = jdbcUrl.split("/");
        String dbNameWithParams = parts[parts.length - 1];
        return dbNameWithParams.split("\\?")[0];
    }

    private String extractHost(String jdbcUrl) {
        // Extract host from JDBC URL
        String[] parts = jdbcUrl.split("//")[1].split("/")[0].split(":");
        return parts[0];
    }

    private String extractPort(String jdbcUrl) {
        // Extract port from JDBC URL
        String[] parts = jdbcUrl.split("//")[1].split("/")[0].split(":");
        return parts.length > 1 ? parts[1] : "5432"; // Default PostgreSQL port
    }

    private void sendBackupAlert(String subject, String message) {
        try {
            String adminEmail = "admin@telanganaballbadminton.org";
            emailService.sendEmail(adminEmail, subject, message);
        } catch (Exception e) {
            logger.error("Failed to send backup alert email: {}", e.getMessage());
        }
    }

    /**
     * Backup information DTO
     */
    public static class BackupInfo {
        private String fileName;
        private String filePath;
        private String type;
        private long size;
        private LocalDateTime timestamp;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}
