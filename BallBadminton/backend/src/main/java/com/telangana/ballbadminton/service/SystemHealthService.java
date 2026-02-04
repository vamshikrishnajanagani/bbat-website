package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.admin.SystemHealthResponse;
import com.telangana.ballbadminton.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for system health monitoring
 * 
 * Provides comprehensive system health information including:
 * - Database connectivity and metrics
 * - Cache performance statistics
 * - Storage capacity and usage
 * - Memory and CPU metrics
 * - Service component status
 * 
 * Requirements: 6.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class SystemHealthService {

    private static final Logger logger = LoggerFactory.getLogger(SystemHealthService.class);

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDirectory;

    private final DataSource dataSource;
    private final CacheManager cacheManager;
    private final MemberRepository memberRepository;
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final MediaItemRepository mediaItemRepository;
    private final DistrictRepository districtRepository;

    public SystemHealthService(
            DataSource dataSource,
            CacheManager cacheManager,
            MemberRepository memberRepository,
            PlayerRepository playerRepository,
            TournamentRepository tournamentRepository,
            NewsArticleRepository newsArticleRepository,
            MediaItemRepository mediaItemRepository,
            DistrictRepository districtRepository) {
        this.dataSource = dataSource;
        this.cacheManager = cacheManager;
        this.memberRepository = memberRepository;
        this.playerRepository = playerRepository;
        this.tournamentRepository = tournamentRepository;
        this.newsArticleRepository = newsArticleRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.districtRepository = districtRepository;
    }

    /**
     * Get comprehensive system health information
     * 
     * @return System health response with all metrics
     */
    public SystemHealthResponse getSystemHealth() {
        logger.info("Collecting system health information");

        SystemHealthResponse response = new SystemHealthResponse();
        
        try {
            // Check all components
            checkDatabaseHealth(response);
            checkCacheHealth(response);
            checkStorageHealth(response);
            checkServicesHealth(response);
            
            // Collect system metrics
            collectSystemMetrics(response);
            
            // Determine overall status
            determineOverallStatus(response);
            
            logger.info("System health check completed: {}", response.getStatus());
            
        } catch (Exception e) {
            logger.error("Error collecting system health: {}", e.getMessage(), e);
            response.setStatus("ERROR");
            response.addComponent("system", new SystemHealthResponse.ComponentHealth(
                "ERROR", "Failed to collect system health: " + e.getMessage()));
        }

        return response;
    }

    /**
     * Check database health and connectivity
     */
    private void checkDatabaseHealth(SystemHealthResponse response) {
        SystemHealthResponse.ComponentHealth dbHealth = new SystemHealthResponse.ComponentHealth();
        
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                dbHealth.setStatus("UP");
                dbHealth.setMessage("Database connection is healthy");
                
                // Get database metadata
                dbHealth.addDetail("database", connection.getMetaData().getDatabaseProductName());
                dbHealth.addDetail("version", connection.getMetaData().getDatabaseProductVersion());
                dbHealth.addDetail("url", connection.getMetaData().getURL());
                
                // Get connection pool stats if available
                try (Statement stmt = connection.createStatement()) {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT count(*) as active_connections FROM pg_stat_activity WHERE state = 'active'");
                    if (rs.next()) {
                        dbHealth.addDetail("activeConnections", rs.getInt("active_connections"));
                    }
                } catch (Exception e) {
                    logger.debug("Could not retrieve connection stats: {}", e.getMessage());
                }
                
            } else {
                dbHealth.setStatus("DOWN");
                dbHealth.setMessage("Database connection is not valid");
            }
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage(), e);
            dbHealth.setStatus("DOWN");
            dbHealth.setMessage("Database connection failed: " + e.getMessage());
        }
        
        response.addComponent("database", dbHealth);
    }

    /**
     * Check cache health and performance
     */
    private void checkCacheHealth(SystemHealthResponse response) {
        SystemHealthResponse.ComponentHealth cacheHealth = new SystemHealthResponse.ComponentHealth();
        
        try {
            if (cacheManager != null) {
                cacheHealth.setStatus("UP");
                cacheHealth.setMessage("Cache manager is operational");
                
                // Get cache names
                cacheHealth.addDetail("cacheNames", cacheManager.getCacheNames());
                
                // Count caches
                int cacheCount = 0;
                for (String cacheName : cacheManager.getCacheNames()) {
                    cacheCount++;
                }
                cacheHealth.addDetail("cacheCount", cacheCount);
                
            } else {
                cacheHealth.setStatus("UNKNOWN");
                cacheHealth.setMessage("Cache manager not available");
            }
        } catch (Exception e) {
            logger.error("Cache health check failed: {}", e.getMessage(), e);
            cacheHealth.setStatus("DOWN");
            cacheHealth.setMessage("Cache check failed: " + e.getMessage());
        }
        
        response.addComponent("cache", cacheHealth);
    }

    /**
     * Check storage health and capacity
     */
    private void checkStorageHealth(SystemHealthResponse response) {
        SystemHealthResponse.ComponentHealth storageHealth = new SystemHealthResponse.ComponentHealth();
        
        try {
            File uploadDir = new File(uploadDirectory);
            
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            long totalSpace = uploadDir.getTotalSpace();
            long usableSpace = uploadDir.getUsableSpace();
            long usedSpace = totalSpace - usableSpace;
            double usagePercent = (double) usedSpace / totalSpace * 100;
            
            storageHealth.setStatus("UP");
            storageHealth.setMessage("Storage is accessible");
            storageHealth.addDetail("totalSpace", formatBytes(totalSpace));
            storageHealth.addDetail("usableSpace", formatBytes(usableSpace));
            storageHealth.addDetail("usedSpace", formatBytes(usedSpace));
            storageHealth.addDetail("usagePercent", String.format("%.2f%%", usagePercent));
            storageHealth.addDetail("uploadDirectory", uploadDir.getAbsolutePath());
            
            // Warn if storage is running low
            if (usagePercent > 90) {
                storageHealth.setStatus("WARNING");
                storageHealth.setMessage("Storage usage is high: " + String.format("%.2f%%", usagePercent));
            }
            
        } catch (Exception e) {
            logger.error("Storage health check failed: {}", e.getMessage(), e);
            storageHealth.setStatus("DOWN");
            storageHealth.setMessage("Storage check failed: " + e.getMessage());
        }
        
        response.addComponent("storage", storageHealth);
    }

    /**
     * Check health of various services
     */
    private void checkServicesHealth(SystemHealthResponse response) {
        // Check if repositories are accessible
        SystemHealthResponse.ComponentHealth servicesHealth = new SystemHealthResponse.ComponentHealth();
        
        try {
            // Test repository access
            memberRepository.count();
            playerRepository.count();
            tournamentRepository.count();
            
            servicesHealth.setStatus("UP");
            servicesHealth.setMessage("All services are operational");
            
        } catch (Exception e) {
            logger.error("Services health check failed: {}", e.getMessage(), e);
            servicesHealth.setStatus("DOWN");
            servicesHealth.setMessage("Service check failed: " + e.getMessage());
        }
        
        response.addComponent("services", servicesHealth);
    }

    /**
     * Collect system metrics
     */
    private void collectSystemMetrics(SystemHealthResponse response) {
        SystemHealthResponse.SystemMetrics metrics = new SystemHealthResponse.SystemMetrics();
        
        try {
            // Memory metrics
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            metrics.setTotalMemory(totalMemory);
            metrics.setFreeMemory(freeMemory);
            metrics.setUsedMemory(usedMemory);
            metrics.setMaxMemory(maxMemory);
            metrics.setMemoryUsagePercent(memoryUsagePercent);
            
            // CPU metrics
            metrics.setAvailableProcessors(runtime.availableProcessors());
            
            // Uptime
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            metrics.setUptime(uptime);
            
            // Database metrics
            SystemHealthResponse.DatabaseMetrics dbMetrics = new SystemHealthResponse.DatabaseMetrics();
            try {
                long memberCount = memberRepository.count();
                long playerCount = playerRepository.count();
                long tournamentCount = tournamentRepository.count();
                long newsCount = newsArticleRepository.count();
                long mediaCount = mediaItemRepository.count();
                long districtCount = districtRepository.count();
                
                Map<String, Long> recordsByEntity = new HashMap<>();
                recordsByEntity.put("members", memberCount);
                recordsByEntity.put("players", playerCount);
                recordsByEntity.put("tournaments", tournamentCount);
                recordsByEntity.put("news", newsCount);
                recordsByEntity.put("media", mediaCount);
                recordsByEntity.put("districts", districtCount);
                
                dbMetrics.setRecordsByEntity(recordsByEntity);
                dbMetrics.setTotalRecords(
                    memberCount + playerCount + tournamentCount + 
                    newsCount + mediaCount + districtCount);
                
            } catch (Exception e) {
                logger.error("Failed to collect database metrics: {}", e.getMessage());
            }
            metrics.setDatabase(dbMetrics);
            
            // Cache metrics
            SystemHealthResponse.CacheMetrics cacheMetrics = new SystemHealthResponse.CacheMetrics();
            // Note: Actual cache metrics would require cache-specific implementation
            cacheMetrics.setHitCount(0);
            cacheMetrics.setMissCount(0);
            cacheMetrics.setHitRate(0.0);
            metrics.setCache(cacheMetrics);
            
            // Storage metrics
            SystemHealthResponse.StorageMetrics storageMetrics = new SystemHealthResponse.StorageMetrics();
            File uploadDir = new File(uploadDirectory);
            if (uploadDir.exists()) {
                storageMetrics.setTotalSpace(uploadDir.getTotalSpace());
                storageMetrics.setUsableSpace(uploadDir.getUsableSpace());
                storageMetrics.setUsedSpace(uploadDir.getTotalSpace() - uploadDir.getUsableSpace());
                storageMetrics.setUsagePercent(
                    (double) (uploadDir.getTotalSpace() - uploadDir.getUsableSpace()) / 
                    uploadDir.getTotalSpace() * 100);
            }
            metrics.setStorage(storageMetrics);
            
            response.setMetrics(metrics);
            
        } catch (Exception e) {
            logger.error("Failed to collect system metrics: {}", e.getMessage(), e);
        }
    }

    /**
     * Determine overall system status based on component health
     */
    private void determineOverallStatus(SystemHealthResponse response) {
        boolean hasDown = false;
        boolean hasWarning = false;
        
        for (SystemHealthResponse.ComponentHealth component : response.getComponents().values()) {
            if ("DOWN".equals(component.getStatus()) || "ERROR".equals(component.getStatus())) {
                hasDown = true;
                break;
            } else if ("WARNING".equals(component.getStatus())) {
                hasWarning = true;
            }
        }
        
        if (hasDown) {
            response.setStatus("DOWN");
        } else if (hasWarning) {
            response.setStatus("WARNING");
        } else {
            response.setStatus("UP");
        }
    }

    /**
     * Format bytes to human-readable format
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Get quick health status (lightweight check)
     */
    public String getQuickHealthStatus() {
        try {
            // Quick database check
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    return "UP";
                }
            }
            return "DOWN";
        } catch (Exception e) {
            logger.error("Quick health check failed: {}", e.getMessage());
            return "DOWN";
        }
    }
}
