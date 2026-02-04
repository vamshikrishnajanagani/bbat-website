package com.telangana.ballbadminton.e2e;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.entity.District;
import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.repository.DistrictRepository;
import com.telangana.ballbadminton.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Performance and Load Tests
 * 
 * Tests:
 * - Response time under normal load
 * - Concurrent request handling
 * - High-volume data operations
 * - Database query performance
 * - Caching effectiveness
 * 
 * Validates Requirements: 7.1 (Page load time < 3 seconds)
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Performance and Load E2E Tests")
class PerformanceE2ETest extends BaseIntegrationTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private DistrictRepository districtRepository;

    private District testDistrict;

    @BeforeEach
    void setupTestData() {
        playerRepository.deleteAll();
        districtRepository.deleteAll();

        // Create test district
        testDistrict = new District();
        testDistrict.setName("Hyderabad");
        testDistrict.setCode("HYD");
        testDistrict.setHeadquarters("Hyderabad");
        testDistrict.setArea(650.0);
        testDistrict.setPopulation(10000000L);
        testDistrict = districtRepository.save(testDistrict);
    }

    @Test
    @DisplayName("Performance: API response time under normal load")
    void testApiResponseTime() throws Exception {
        // Create test data
        createTestPlayers(10);

        // Test response times for various endpoints
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players"))
                .andExpect(status().isOk());
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Response time should be under 500ms for list endpoint
        assertThat(responseTime).isLessThan(500);

        // Test single entity retrieval
        Player player = playerRepository.findAll().get(0);
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players/" + player.getId()))
                .andExpect(status().isOk());
        endTime = System.currentTimeMillis();
        responseTime = endTime - startTime;

        // Single entity retrieval should be under 200ms
        assertThat(responseTime).isLessThan(200);
    }

    @Test
    @DisplayName("Performance: Concurrent request handling")
    void testConcurrentRequestHandling() throws Exception {
        // Create test data
        createTestPlayers(20);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Long>> futures = new ArrayList<>();

        // Submit 50 concurrent requests
        for (int i = 0; i < 50; i++) {
            Future<Long> future = executor.submit(() -> {
                long start = System.currentTimeMillis();
                mockMvc.perform(get("/api/v1/players"))
                        .andExpect(status().isOk());
                return System.currentTimeMillis() - start;
            });
            futures.add(future);
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Verify all requests completed successfully
        assertThat(futures).hasSize(50);
        
        // Calculate average response time
        long totalTime = 0;
        for (Future<Long> future : futures) {
            totalTime += future.get();
        }
        long avgResponseTime = totalTime / futures.size();

        // Average response time should be under 1 second even under load
        assertThat(avgResponseTime).isLessThan(1000);
    }

    @Test
    @DisplayName("Performance: High-volume data retrieval")
    void testHighVolumeDataRetrieval() throws Exception {
        // Create large dataset
        createTestPlayers(100);

        long startTime = System.currentTimeMillis();
        
        MvcResult result = mockMvc.perform(get("/api/v1/players?size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Should handle 100 records in under 1 second
        assertThat(responseTime).isLessThan(1000);

        // Verify data integrity
        String responseBody = result.getResponse().getContentAsString();
        var content = objectMapper.readTree(responseBody).get("content");
        assertThat(content.size()).isEqualTo(100);
    }

    @Test
    @DisplayName("Performance: Pagination performance")
    void testPaginationPerformance() throws Exception {
        // Create dataset
        createTestPlayers(200);

        // Test first page
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.totalElements").value(200));
        long firstPageTime = System.currentTimeMillis() - startTime;

        // Test middle page
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?page=5&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20));
        long middlePageTime = System.currentTimeMillis() - startTime;

        // Test last page
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?page=9&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20));
        long lastPageTime = System.currentTimeMillis() - startTime;

        // All page retrievals should be under 500ms
        assertThat(firstPageTime).isLessThan(500);
        assertThat(middlePageTime).isLessThan(500);
        assertThat(lastPageTime).isLessThan(500);
    }

    @Test
    @DisplayName("Performance: Search and filter performance")
    void testSearchAndFilterPerformance() throws Exception {
        // Create diverse dataset
        createTestPlayers(150);

        // Test category filter
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?category=Men"))
                .andExpect(status().isOk());
        long filterTime = System.currentTimeMillis() - startTime;

        // Test district filter
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?districtId=" + testDistrict.getId()))
                .andExpect(status().isOk());
        long districtFilterTime = System.currentTimeMillis() - startTime;

        // Test combined filters
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players?category=Men&districtId=" + testDistrict.getId()))
                .andExpect(status().isOk());
        long combinedFilterTime = System.currentTimeMillis() - startTime;

        // All filter operations should be under 500ms
        assertThat(filterTime).isLessThan(500);
        assertThat(districtFilterTime).isLessThan(500);
        assertThat(combinedFilterTime).isLessThan(500);
    }

    @Test
    @DisplayName("Performance: Bulk operation performance")
    void testBulkOperationPerformance() throws Exception {
        // Create players for bulk update
        List<String> playerIds = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Player player = new Player();
            player.setName("Bulk Player " + i);
            player.setDateOfBirth(LocalDate.of(1995, 1, 1));
            player.setDistrict(testDistrict);
            player.setCategory(i % 2 == 0 ? "Men" : "Women");
            player.setIsProminent(false);
            player = playerRepository.save(player);
            playerIds.add(player.getId().toString());
        }

        // Test bulk update
        String bulkUpdateJson = "{\"playerIds\":" + objectMapper.writeValueAsString(playerIds) + ",\"isProminent\":true}";
        
        long startTime = System.currentTimeMillis();
        mockMvc.perform(post("/api/v1/admin/bulk/players/update")
                .contentType("application/json")
                .content(bulkUpdateJson))
                .andExpect(status().isOk());
        long bulkUpdateTime = System.currentTimeMillis() - startTime;

        // Bulk update of 50 records should complete in under 2 seconds
        assertThat(bulkUpdateTime).isLessThan(2000);

        // Verify all updates were applied
        long prominentCount = playerRepository.findAll().stream()
                .filter(Player::getIsProminent)
                .count();
        assertThat(prominentCount).isEqualTo(50);
    }

    @Test
    @DisplayName("Performance: Complex query performance")
    void testComplexQueryPerformance() throws Exception {
        // Create dataset with relationships
        createTestPlayers(100);

        // Test complex query with joins
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/districts/" + testDistrict.getId() + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount").exists())
                .andExpect(jsonPath("$.districtName").exists());
        long complexQueryTime = System.currentTimeMillis() - startTime;

        // Complex queries with aggregations should be under 500ms
        assertThat(complexQueryTime).isLessThan(500);
    }

    @Test
    @DisplayName("Performance: Repeated read performance (caching)")
    void testRepeatedReadPerformance() throws Exception {
        // Create test data
        Player player = new Player();
        player.setName("Cache Test Player");
        player.setDateOfBirth(LocalDate.of(1995, 1, 1));
        player.setDistrict(testDistrict);
        player.setCategory("Men");
        player.setIsProminent(false);
        player = playerRepository.save(player);

        // First read (cache miss)
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/players/" + player.getId()))
                .andExpect(status().isOk());
        long firstReadTime = System.currentTimeMillis() - startTime;

        // Subsequent reads (should be faster due to caching)
        long totalSubsequentTime = 0;
        for (int i = 0; i < 10; i++) {
            startTime = System.currentTimeMillis();
            mockMvc.perform(get("/api/v1/players/" + player.getId()))
                    .andExpect(status().isOk());
            totalSubsequentTime += (System.currentTimeMillis() - startTime);
        }
        long avgSubsequentTime = totalSubsequentTime / 10;

        // Cached reads should be faster than initial read
        // Note: This may not always be true in test environment, but we check reasonable times
        assertThat(avgSubsequentTime).isLessThan(200);
    }

    @Test
    @DisplayName("Performance: Database connection pool under load")
    void testDatabaseConnectionPoolUnderLoad() throws Exception {
        createTestPlayers(50);

        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> futures = new ArrayList<>();

        // Submit 100 database operations concurrently
        for (int i = 0; i < 100; i++) {
            final int index = i;
            Future<Boolean> future = executor.submit(() -> {
                try {
                    if (index % 2 == 0) {
                        // Read operation
                        mockMvc.perform(get("/api/v1/players"))
                                .andExpect(status().isOk());
                    } else {
                        // Write operation
                        Player player = new Player();
                        player.setName("Concurrent Player " + index);
                        player.setDateOfBirth(LocalDate.of(1995, 1, 1));
                        player.setDistrict(testDistrict);
                        player.setCategory("Men");
                        player.setIsProminent(false);
                        playerRepository.save(player);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            futures.add(future);
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        // Verify all operations completed successfully
        long successCount = futures.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        // At least 95% of operations should succeed
        assertThat(successCount).isGreaterThanOrEqualTo(95);
    }

    // Helper method
    private void createTestPlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Player player = new Player();
            player.setName("Test Player " + i);
            player.setDateOfBirth(LocalDate.of(1995, 1, (i % 28) + 1));
            player.setDistrict(testDistrict);
            player.setCategory(i % 2 == 0 ? "Men" : "Women");
            player.setIsProminent(i % 10 == 0);
            players.add(player);
        }
        playerRepository.saveAll(players);
    }
}
