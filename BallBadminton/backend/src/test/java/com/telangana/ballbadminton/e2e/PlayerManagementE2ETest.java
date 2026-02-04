package com.telangana.ballbadminton.e2e;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.dto.player.CreatePlayerRequest;
import com.telangana.ballbadminton.dto.player.UpdatePlayerRequest;
import com.telangana.ballbadminton.entity.Achievement;
import com.telangana.ballbadminton.entity.District;
import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.repository.AchievementRepository;
import com.telangana.ballbadminton.repository.DistrictRepository;
import com.telangana.ballbadminton.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End tests for Player Management functionality
 * 
 * Tests critical user journeys:
 * - Creating player profiles
 * - Managing achievements
 * - Player statistics and rankings
 * - Search and filtering
 * - Historical data preservation
 * 
 * Validates Requirements: 3.1, 3.2, 3.3, 3.4, 3.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Player Management E2E Tests")
class PlayerManagementE2ETest extends BaseIntegrationTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    private District testDistrict;

    @BeforeEach
    void setupTestData() {
        achievementRepository.deleteAll();
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
    @DisplayName("E2E: Complete player lifecycle with achievements")
    void testCompletePlayerLifecycle() throws Exception {
        // Step 1: Create a new player
        CreatePlayerRequest createRequest = new CreatePlayerRequest();
        createRequest.setName("Arjun Reddy");
        createRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        createRequest.setDistrictId(testDistrict.getId());
        createRequest.setCategory("Men");
        createRequest.setIsProminent(true);

        MvcResult createResult = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Arjun Reddy"))
                .andExpect(jsonPath("$.category").value("Men"))
                .andExpect(jsonPath("$.district.name").value("Hyderabad"))
                .andReturn();

        String playerId = extractIdFromResponse(createResult);

        // Step 2: Add achievements to player
        mockMvc.perform(post("/api/v1/players/" + playerId + "/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"State Champion 2023\",\"description\":\"Won state championship\",\"date\":\"2023-06-15\",\"category\":\"Championship\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("State Champion 2023"));

        mockMvc.perform(post("/api/v1/players/" + playerId + "/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"National Runner-up 2023\",\"description\":\"Runner-up in nationals\",\"date\":\"2023-09-20\",\"category\":\"Championship\"}"))
                .andExpect(status().isCreated());

        // Step 3: Retrieve player with achievements
        mockMvc.perform(get("/api/v1/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements").isArray())
                .andExpect(jsonPath("$.achievements.length()").value(2));

        // Step 4: Update player information
        UpdatePlayerRequest updateRequest = new UpdatePlayerRequest();
        updateRequest.setIsProminent(true);

        mockMvc.perform(put("/api/v1/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isOk());

        // Step 5: Verify achievements are preserved after update (Historical Data Preservation)
        mockMvc.perform(get("/api/v1/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements").isArray())
                .andExpect(jsonPath("$.achievements.length()").value(2))
                .andExpect(jsonPath("$.achievements[0].title").exists())
                .andExpect(jsonPath("$.achievements[1].title").exists());

        // Verify in database
        Player savedPlayer = playerRepository.findById(UUID.fromString(playerId)).orElse(null);
        assertThat(savedPlayer).isNotNull();
        List<Achievement> achievements = achievementRepository.findByPlayerId(UUID.fromString(playerId));
        assertThat(achievements).hasSize(2);
    }

    @Test
    @DisplayName("E2E: Player search and filtering by district")
    void testPlayerSearchAndFiltering() throws Exception {
        // Create players in different districts
        District district2 = new District();
        district2.setName("Warangal");
        district2.setCode("WGL");
        district2.setHeadquarters("Warangal");
        district2.setArea(500.0);
        district2.setPopulation(3000000L);
        district2 = districtRepository.save(district2);

        createPlayer("Player 1", testDistrict.getId(), "Men");
        createPlayer("Player 2", testDistrict.getId(), "Women");
        createPlayer("Player 3", district2.getId(), "Men");

        // Filter by district
        mockMvc.perform(get("/api/v1/players?districtId=" + testDistrict.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        // Filter by category
        mockMvc.perform(get("/api/v1/players?category=Men"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        // Combined filters
        mockMvc.perform(get("/api/v1/players?districtId=" + testDistrict.getId() + "&category=Men"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("E2E: Player ranking calculation and ordering")
    void testPlayerRankingCalculation() throws Exception {
        // Create players with different statistics
        String player1Id = createPlayerWithStats("Top Player", 100, 80);
        String player2Id = createPlayerWithStats("Mid Player", 50, 30);
        String player3Id = createPlayerWithStats("New Player", 10, 5);

        // Get ranked players
        MvcResult result = mockMvc.perform(get("/api/v1/players/rankings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        var players = objectMapper.readTree(responseBody).get("content");

        // Verify ranking order (highest win percentage first)
        assertThat(players.get(0).get("name").asText()).isEqualTo("Top Player");
        assertThat(players.get(1).get("name").asText()).isEqualTo("Mid Player");
        assertThat(players.get(2).get("name").asText()).isEqualTo("New Player");
    }

    @Test
    @DisplayName("E2E: Player statistics aggregation")
    void testPlayerStatisticsAggregation() throws Exception {
        // Create player
        String playerId = createPlayer("Stats Player", testDistrict.getId(), "Men");

        // Update statistics
        mockMvc.perform(post("/api/v1/players/" + playerId + "/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"matchesPlayed\":50,\"matchesWon\":35,\"tournamentsParticipated\":10,\"tournamentsWon\":3}"))
                .andExpect(status().isOk());

        // Retrieve and verify statistics
        mockMvc.perform(get("/api/v1/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statistics.matchesPlayed").value(50))
                .andExpect(jsonPath("$.statistics.matchesWon").value(35))
                .andExpect(jsonPath("$.statistics.winPercentage").value(70.0));
    }

    @Test
    @DisplayName("E2E: Prominent players listing")
    void testProminentPlayersListing() throws Exception {
        // Create mix of prominent and regular players
        createPlayerWithProminence("Prominent 1", true);
        createPlayerWithProminence("Regular 1", false);
        createPlayerWithProminence("Prominent 2", true);
        createPlayerWithProminence("Regular 2", false);

        // Get only prominent players
        mockMvc.perform(get("/api/v1/players/prominent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        // Get all players
        mockMvc.perform(get("/api/v1/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(4));
    }

    @Test
    @DisplayName("E2E: Player data validation")
    void testPlayerDataValidation() throws Exception {
        // Test invalid date of birth (future date)
        CreatePlayerRequest invalidRequest = new CreatePlayerRequest();
        invalidRequest.setName("Invalid Player");
        invalidRequest.setDateOfBirth(LocalDate.now().plusYears(1));
        invalidRequest.setDistrictId(testDistrict.getId());
        invalidRequest.setCategory("Men");

        mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Test invalid category
        CreatePlayerRequest invalidCategoryRequest = new CreatePlayerRequest();
        invalidCategoryRequest.setName("Test Player");
        invalidCategoryRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        invalidCategoryRequest.setDistrictId(testDistrict.getId());
        invalidCategoryRequest.setCategory("InvalidCategory");

        mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidCategoryRequest)))
                .andExpect(status().isBadRequest());
    }

    // Helper methods
    private String createPlayer(String name, UUID districtId, String category) throws Exception {
        CreatePlayerRequest request = new CreatePlayerRequest();
        request.setName(name);
        request.setDateOfBirth(LocalDate.of(1995, 1, 1));
        request.setDistrictId(districtId);
        request.setCategory(category);
        request.setIsProminent(false);

        MvcResult result = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return extractIdFromResponse(result);
    }

    private String createPlayerWithStats(String name, int matchesPlayed, int matchesWon) throws Exception {
        String playerId = createPlayer(name, testDistrict.getId(), "Men");

        mockMvc.perform(post("/api/v1/players/" + playerId + "/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"matchesPlayed\":" + matchesPlayed + ",\"matchesWon\":" + matchesWon + ",\"tournamentsParticipated\":10,\"tournamentsWon\":2}"))
                .andExpect(status().isOk());

        return playerId;
    }

    private String createPlayerWithProminence(String name, boolean isProminent) throws Exception {
        CreatePlayerRequest request = new CreatePlayerRequest();
        request.setName(name);
        request.setDateOfBirth(LocalDate.of(1995, 1, 1));
        request.setDistrictId(testDistrict.getId());
        request.setCategory("Men");
        request.setIsProminent(isProminent);

        MvcResult result = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return extractIdFromResponse(result);
    }

    private String extractIdFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asText();
    }
}
