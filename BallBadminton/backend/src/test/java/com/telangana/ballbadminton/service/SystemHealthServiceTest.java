package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.dto.admin.SystemHealthResponse;
import com.telangana.ballbadminton.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SystemHealthService
 * 
 * Tests system health monitoring including:
 * - Database connectivity checks
 * - Cache health monitoring
 * - Storage capacity checks
 * - Service component status
 * - System metrics collection
 * 
 * Requirements: 6.5
 */
@DisplayName("SystemHealthService Tests")
class SystemHealthServiceTest extends BaseUnitTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @Mock
    private MediaItemRepository mediaItemRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    @InjectMocks
    private SystemHealthService systemHealthService;

    @BeforeEach
    void setupTest() {
        ReflectionTestUtils.setField(systemHealthService, "uploadDirectory", "./uploads");
    }

    @Test
    @DisplayName("Should return UP status when all components are healthy")
    void testGetSystemHealth_AllComponentsHealthy() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("cache1", "cache2"));

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);
        when(newsArticleRepository.count()).thenReturn(30L);
        when(mediaItemRepository.count()).thenReturn(100L);
        when(districtRepository.count()).thenReturn(33L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("UP");
        assertThat(response.getComponents()).containsKeys("database", "cache", "storage", "services");
        assertThat(response.getMetrics()).isNotNull();
    }

    @Test
    @DisplayName("Should return DOWN status when database is unhealthy")
    void testGetSystemHealth_DatabaseDown() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(false);

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("cache1"));

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("DOWN");
        assertThat(response.getComponents().get("database").getStatus()).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Should check database connectivity")
    void testGetSystemHealth_DatabaseConnectivity() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());
        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.ComponentHealth dbHealth = response.getComponents().get("database");
        assertThat(dbHealth).isNotNull();
        assertThat(dbHealth.getStatus()).isEqualTo("UP");
        assertThat(dbHealth.getMessage()).contains("Database connection is healthy");
        assertThat(dbHealth.getDetails()).containsKeys("database", "version", "url");
    }

    @Test
    @DisplayName("Should check cache health")
    void testGetSystemHealth_CacheHealth() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("members", "players", "tournaments"));

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.ComponentHealth cacheHealth = response.getComponents().get("cache");
        assertThat(cacheHealth).isNotNull();
        assertThat(cacheHealth.getStatus()).isEqualTo("UP");
        assertThat(cacheHealth.getDetails().get("cacheCount")).isEqualTo(3);
    }

    @Test
    @DisplayName("Should check storage health")
    void testGetSystemHealth_StorageHealth() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.ComponentHealth storageHealth = response.getComponents().get("storage");
        assertThat(storageHealth).isNotNull();
        assertThat(storageHealth.getStatus()).isIn("UP", "WARNING");
        assertThat(storageHealth.getDetails()).containsKeys("totalSpace", "usableSpace", "usedSpace", "usagePercent");
    }

    @Test
    @DisplayName("Should check services health")
    void testGetSystemHealth_ServicesHealth() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.ComponentHealth servicesHealth = response.getComponents().get("services");
        assertThat(servicesHealth).isNotNull();
        assertThat(servicesHealth.getStatus()).isEqualTo("UP");
    }

    @Test
    @DisplayName("Should collect system metrics")
    void testGetSystemHealth_SystemMetrics() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);
        when(newsArticleRepository.count()).thenReturn(30L);
        when(mediaItemRepository.count()).thenReturn(100L);
        when(districtRepository.count()).thenReturn(33L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.SystemMetrics metrics = response.getMetrics();
        assertThat(metrics).isNotNull();
        assertThat(metrics.getTotalMemory()).isGreaterThan(0);
        assertThat(metrics.getMaxMemory()).isGreaterThan(0);
        assertThat(metrics.getAvailableProcessors()).isGreaterThan(0);
        assertThat(metrics.getUptime()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should collect database metrics")
    void testGetSystemHealth_DatabaseMetrics() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());

        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);
        when(newsArticleRepository.count()).thenReturn(30L);
        when(mediaItemRepository.count()).thenReturn(100L);
        when(districtRepository.count()).thenReturn(33L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        SystemHealthResponse.DatabaseMetrics dbMetrics = response.getMetrics().getDatabase();
        assertThat(dbMetrics).isNotNull();
        assertThat(dbMetrics.getTotalRecords()).isEqualTo(243L); // Sum of all counts
        assertThat(dbMetrics.getRecordsByEntity()).containsKeys("members", "players", "tournaments", "news", "media", "districts");
        assertThat(dbMetrics.getRecordsByEntity().get("members")).isEqualTo(10L);
        assertThat(dbMetrics.getRecordsByEntity().get("players")).isEqualTo(50L);
    }

    @Test
    @DisplayName("Should return UP for quick health check when database is accessible")
    void testGetQuickHealthStatus_DatabaseAccessible() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        // Act
        String status = systemHealthService.getQuickHealthStatus();

        // Assert
        assertThat(status).isEqualTo("UP");
    }

    @Test
    @DisplayName("Should return DOWN for quick health check when database is not accessible")
    void testGetQuickHealthStatus_DatabaseNotAccessible() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        // Act
        String status = systemHealthService.getQuickHealthStatus();

        // Assert
        assertThat(status).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Should return DOWN for quick health check on exception")
    void testGetQuickHealthStatus_Exception() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        // Act
        String status = systemHealthService.getQuickHealthStatus();

        // Assert
        assertThat(status).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Should handle database connection exception gracefully")
    void testGetSystemHealth_DatabaseException() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());
        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("DOWN");
        assertThat(response.getComponents().get("database").getStatus()).isEqualTo("DOWN");
        assertThat(response.getComponents().get("database").getMessage()).contains("Connection failed");
    }

    @Test
    @DisplayName("Should handle services exception gracefully")
    void testGetSystemHealth_ServicesException() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());

        when(memberRepository.count()).thenThrow(new RuntimeException("Repository error"));

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("DOWN");
        assertThat(response.getComponents().get("services").getStatus()).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Should include timestamp in health response")
    void testGetSystemHealth_IncludesTimestamp() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("14.0");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/testdb");

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList());
        when(memberRepository.count()).thenReturn(10L);
        when(playerRepository.count()).thenReturn(50L);
        when(tournamentRepository.count()).thenReturn(20L);

        // Act
        SystemHealthResponse response = systemHealthService.getSystemHealth();

        // Assert
        assertThat(response.getTimestamp()).isNotNull();
    }
}
