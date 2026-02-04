package com.telangana.ballbadminton.dto.admin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Response DTO for system health monitoring
 * 
 * Provides comprehensive system health information
 * including database, cache, storage, and service status
 * 
 * Requirements: 6.5
 */
public class SystemHealthResponse {

    private String status;
    private LocalDateTime timestamp;
    private Map<String, ComponentHealth> components;
    private SystemMetrics metrics;
    private Map<String, Object> configuration;

    public SystemHealthResponse() {
        this.components = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }

    public static class ComponentHealth {
        private String status;
        private String message;
        private Map<String, Object> details;
        private LocalDateTime lastChecked;

        public ComponentHealth() {
            this.details = new HashMap<>();
            this.lastChecked = LocalDateTime.now();
        }

        public ComponentHealth(String status, String message) {
            this();
            this.status = status;
            this.message = message;
        }

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }

        public LocalDateTime getLastChecked() {
            return lastChecked;
        }

        public void setLastChecked(LocalDateTime lastChecked) {
            this.lastChecked = lastChecked;
        }

        public void addDetail(String key, Object value) {
            this.details.put(key, value);
        }
    }

    public static class SystemMetrics {
        private long totalMemory;
        private long freeMemory;
        private long usedMemory;
        private long maxMemory;
        private double memoryUsagePercent;
        private int availableProcessors;
        private long uptime;
        private DatabaseMetrics database;
        private CacheMetrics cache;
        private StorageMetrics storage;

        public SystemMetrics() {}

        // Getters and Setters
        public long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public long getFreeMemory() {
            return freeMemory;
        }

        public void setFreeMemory(long freeMemory) {
            this.freeMemory = freeMemory;
        }

        public long getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(long usedMemory) {
            this.usedMemory = usedMemory;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(long maxMemory) {
            this.maxMemory = maxMemory;
        }

        public double getMemoryUsagePercent() {
            return memoryUsagePercent;
        }

        public void setMemoryUsagePercent(double memoryUsagePercent) {
            this.memoryUsagePercent = memoryUsagePercent;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        public void setAvailableProcessors(int availableProcessors) {
            this.availableProcessors = availableProcessors;
        }

        public long getUptime() {
            return uptime;
        }

        public void setUptime(long uptime) {
            this.uptime = uptime;
        }

        public DatabaseMetrics getDatabase() {
            return database;
        }

        public void setDatabase(DatabaseMetrics database) {
            this.database = database;
        }

        public CacheMetrics getCache() {
            return cache;
        }

        public void setCache(CacheMetrics cache) {
            this.cache = cache;
        }

        public StorageMetrics getStorage() {
            return storage;
        }

        public void setStorage(StorageMetrics storage) {
            this.storage = storage;
        }
    }

    public static class DatabaseMetrics {
        private long totalConnections;
        private long activeConnections;
        private long idleConnections;
        private long totalRecords;
        private Map<String, Long> recordsByEntity;

        public DatabaseMetrics() {
            this.recordsByEntity = new HashMap<>();
        }

        // Getters and Setters
        public long getTotalConnections() {
            return totalConnections;
        }

        public void setTotalConnections(long totalConnections) {
            this.totalConnections = totalConnections;
        }

        public long getActiveConnections() {
            return activeConnections;
        }

        public void setActiveConnections(long activeConnections) {
            this.activeConnections = activeConnections;
        }

        public long getIdleConnections() {
            return idleConnections;
        }

        public void setIdleConnections(long idleConnections) {
            this.idleConnections = idleConnections;
        }

        public long getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(long totalRecords) {
            this.totalRecords = totalRecords;
        }

        public Map<String, Long> getRecordsByEntity() {
            return recordsByEntity;
        }

        public void setRecordsByEntity(Map<String, Long> recordsByEntity) {
            this.recordsByEntity = recordsByEntity;
        }
    }

    public static class CacheMetrics {
        private long hitCount;
        private long missCount;
        private double hitRate;
        private long evictionCount;
        private long size;

        public CacheMetrics() {}

        // Getters and Setters
        public long getHitCount() {
            return hitCount;
        }

        public void setHitCount(long hitCount) {
            this.hitCount = hitCount;
        }

        public long getMissCount() {
            return missCount;
        }

        public void setMissCount(long missCount) {
            this.missCount = missCount;
        }

        public double getHitRate() {
            return hitRate;
        }

        public void setHitRate(double hitRate) {
            this.hitRate = hitRate;
        }

        public long getEvictionCount() {
            return evictionCount;
        }

        public void setEvictionCount(long evictionCount) {
            this.evictionCount = evictionCount;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }

    public static class StorageMetrics {
        private long totalSpace;
        private long usableSpace;
        private long usedSpace;
        private double usagePercent;
        private long uploadedFiles;

        public StorageMetrics() {}

        // Getters and Setters
        public long getTotalSpace() {
            return totalSpace;
        }

        public void setTotalSpace(long totalSpace) {
            this.totalSpace = totalSpace;
        }

        public long getUsableSpace() {
            return usableSpace;
        }

        public void setUsableSpace(long usableSpace) {
            this.usableSpace = usableSpace;
        }

        public long getUsedSpace() {
            return usedSpace;
        }

        public void setUsedSpace(long usedSpace) {
            this.usedSpace = usedSpace;
        }

        public double getUsagePercent() {
            return usagePercent;
        }

        public void setUsagePercent(double usagePercent) {
            this.usagePercent = usagePercent;
        }

        public long getUploadedFiles() {
            return uploadedFiles;
        }

        public void setUploadedFiles(long uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
        }
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, ComponentHealth> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ComponentHealth> components) {
        this.components = components;
    }

    public SystemMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(SystemMetrics metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public void addComponent(String name, ComponentHealth health) {
        this.components.put(name, health);
    }
}
