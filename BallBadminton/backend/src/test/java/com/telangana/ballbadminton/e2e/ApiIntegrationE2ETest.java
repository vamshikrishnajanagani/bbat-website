package com.telangana.ballbadminton.e2e;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.entity.*;
import com.telangana.ballbadminton.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End tests for API Integration and Data Consistency
 * 
 * Tests:
 * - Cross-entity data consistency
 * - API endpoint completeness
 * - Data round-trip consistency
 * - Real-time update propagation
 * - Referential integrity
 * 
 * Validates Requirements: 7.1, 9.1, 9.2, 9.4, 9.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("API Integration and Data Consistency E2E Tests")
class ApiIntegrationE2ETest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private NewsArticleRepository newsRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void cleanDatabase() {
        auditLogRepository.deleteAll();
        newsRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
        memberRepository.deleteAll();
        districtRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Data round-trip consistency for all entity types")
    void testDataRoundTripConsistency() throws Exception {
        // Test Member round-trip
        String memberJson = "{\"name\":\"Test Member\",\"position\":\"President\",\"email\":\"test@example.com\",\"phone\":\"+91-9876543210\",\"biography\":\"Test bio\",\"hierarchy\":1,\"tenureStartDate\":\"2023-01-01\"}";
        
        MvcResult memberResult = mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isCreated())
                .andReturn();

        String memberId = extractIdFromResponse(memberResult);

        mockMvc.perform(get("/api/v1/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Member"))
                .andExpect(jsonPath("$.position").value("President"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phone").value("+91-9876543210"))
                .andExpect(jsonPath("$.biography").value("Test bio"))
                .andExpect(jsonPath("$.hierarchy").value(1));

        // Test District round-trip
        String districtJson = "{\"name\":\"Hyderabad\",\"code\":\"HYD\",\"headquarters\":\"Hyderabad\",\"area\":650.0,\"population\":10000000}";
        
        MvcResult districtResult = mockMvc.perform(post("/api/v1/districts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(districtJson))
                .andExpect(status().isCreated())
                .andReturn();

        String districtId = extractIdFromResponse(districtResult);

        mockMvc.perform(get("/api/v1/districts/" + districtId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hyderabad"))
                .andExpect(jsonPath("$.code").value("HYD"))
                .andExpect(jsonPath("$.headquarters").value("Hyderabad"))
                .andExpect(jsonPath("$.area").value(650.0))
                .andExpect(jsonPath("$.population").value(10000000));

        // Test Player round-trip
        String playerJson = "{\"name\":\"Test Player\",\"dateOfBirth\":\"1995-05-15\",\"districtId\":\"" + districtId + "\",\"category\":\"Men\",\"isProminent\":true}";
        
        MvcResult playerResult = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playerJson))
                .andExpect(status().isCreated())
                .andReturn();

        String playerId = extractIdFromResponse(playerResult);

        mockMvc.perform(get("/api/v1/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Player"))
                .andExpect(jsonPath("$.dateOfBirth").value("1995-05-15"))
                .andExpect(jsonPath("$.category").value("Men"))
                .andExpect(jsonPath("$.isProminent").value(true))
                .andExpect(jsonPath("$.district.id").value(districtId));
    }

    @Test
    @DisplayName("E2E: Real-time update propagation across all endpoints")
    void testRealTimeUpdatePropagation() throws Exception {
        // Create district
        District district = new District();
        district.setName("Warangal");
        district.setCode("WGL");
        district.setHeadquarters("Warangal");
        district.setArea(500.0);
        district.setPopulation(3000000L);
        district = districtRepository.save(district);

        // Create player
        String playerJson = "{\"name\":\"Update Test Player\",\"dateOfBirth\":\"1995-01-01\",\"districtId\":\"" + district.getId() + "\",\"category\":\"Men\",\"isProminent\":false}";
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playerJson))
                .andExpect(status().isCreated())
                .andReturn();

        String playerId = extractIdFromResponse(createResult);

        // Update player
        String updateJson = "{\"isProminent\":true}";
        
        mockMvc.perform(put("/api/v1/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isProminent").value(true));

        // Verify update is immediately reflected in:
        // 1. Direct GET
        mockMvc.perform(get("/api/v1/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isProminent").value(true));

        // 2. List endpoint
        mockMvc.perform(get("/api/v1/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id=='" + playerId + "')].isProminent").value(true));

        // 3. Prominent players endpoint
        mockMvc.perform(get("/api/v1/players/prominent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id=='" + playerId + "')]").exists());

        // 4. District's players endpoint
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id=='" + playerId + "')].isProminent").value(true));
    }

    @Test
    @DisplayName("E2E: API endpoint completeness for all content types")
    void testApiEndpointCompleteness() throws Exception {
        // Test CRUD operations for Members
        testCrudOperations("/api/v1/members", 
                "{\"name\":\"Test Member\",\"position\":\"Secretary\",\"email\":\"sec@test.com\",\"phone\":\"+91-1234567890\",\"biography\":\"Bio\",\"hierarchy\":3,\"tenureStartDate\":\"2023-01-01\"}",
                "{\"phone\":\"+91-9999999999\"}");

        // Test CRUD operations for Districts
        testCrudOperations("/api/v1/districts",
                "{\"name\":\"Nizamabad\",\"code\":\"NZB\",\"headquarters\":\"Nizamabad\",\"area\":400.0,\"population\":2500000}",
                "{\"population\":2600000}");

        // Test CRUD operations for News
        testCrudOperations("/api/v1/news",
                "{\"title\":\"Test News\",\"content\":\"Test content\",\"category\":\"Announcement\",\"publishDate\":\"" + LocalDate.now() + "\"}",
                "{\"title\":\"Updated News\"}");
    }

    @Test
    @DisplayName("E2E: Cross-entity referential integrity")
    void testCrossEntityReferentialIntegrity() throws Exception {
        // Create district
        District district = new District();
        district.setName("Karimnagar");
        district.setCode("KMR");
        district.setHeadquarters("Karimnagar");
        district.setArea(450.0);
        district.setPopulation(2800000L);
        district = districtRepository.save(district);

        // Create player in district
        Player player = new Player();
        player.setName("Integrity Test Player");
        player.setDateOfBirth(LocalDate.of(1995, 1, 1));
        player.setDistrict(district);
        player.setCategory("Men");
        player.setIsProminent(false);
        player = playerRepository.save(player);

        // Create tournament in district
        Tournament tournament = new Tournament();
        tournament.setName("Integrity Test Tournament");
        tournament.setDescription("Test");
        tournament.setStartDate(LocalDate.now().plusMonths(1));
        tournament.setEndDate(LocalDate.now().plusMonths(1).plusDays(3));
        tournament.setVenue("Test Venue");
        tournament.setDistrict(district);
        tournament.setRegistrationDeadline(LocalDate.now().plusDays(15));
        tournament.setMaxParticipants(32);
        tournament.setEntryFee(500.0);
        tournament.setStatus("Upcoming");
        tournament = tournamentRepository.save(tournament);

        // Verify player references valid district
        mockMvc.perform(get("/api/v1/players/" + player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.district.id").value(district.getId().toString()))
                .andExpect(jsonPath("$.district.name").value("Karimnagar"));

        // Verify tournament references valid district
        mockMvc.perform(get("/api/v1/tournaments/" + tournament.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.district.id").value(district.getId().toString()))
                .andExpect(jsonPath("$.district.name").value("Karimnagar"));

        // Verify district shows associated players
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id=='" + player.getId() + "')]").exists());

        // Verify district shows associated tournaments
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id=='" + tournament.getId() + "')]").exists());
    }

    @Test
    @DisplayName("E2E: District player count aggregation accuracy")
    void testDistrictPlayerCountAggregation() throws Exception {
        // Create district
        District district = new District();
        district.setName("Khammam");
        district.setCode("KHM");
        district.setHeadquarters("Khammam");
        district.setArea(400.0);
        district.setPopulation(2500000L);
        district = districtRepository.save(district);

        // Initially no players
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount").value(0));

        // Add 3 players
        for (int i = 1; i <= 3; i++) {
            Player player = new Player();
            player.setName("Player " + i);
            player.setDateOfBirth(LocalDate.of(1995, 1, i));
            player.setDistrict(district);
            player.setCategory("Men");
            player.setIsProminent(false);
            playerRepository.save(player);
        }

        // Verify count is updated
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount").value(3));

        // Add 2 more players
        for (int i = 4; i <= 5; i++) {
            Player player = new Player();
            player.setName("Player " + i);
            player.setDateOfBirth(LocalDate.of(1995, 1, i));
            player.setDistrict(district);
            player.setCategory("Women");
            player.setIsProminent(false);
            playerRepository.save(player);
        }

        // Verify count is accurate
        mockMvc.perform(get("/api/v1/districts/" + district.getId() + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount").value(5));
    }

    @Test
    @DisplayName("E2E: Audit trail creation for all modifications")
    void testAuditTrailCreation() throws Exception {
        // Create member
        String memberJson = "{\"name\":\"Audit Test Member\",\"position\":\"Treasurer\",\"email\":\"treasurer@test.com\",\"phone\":\"+91-1111111111\",\"biography\":\"Bio\",\"hierarchy\":4,\"tenureStartDate\":\"2023-01-01\"}";
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isCreated())
                .andReturn();

        String memberId = extractIdFromResponse(createResult);

        // Update member
        mockMvc.perform(put("/api/v1/members/" + memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"phone\":\"+91-2222222222\"}"))
                .andExpect(status().isOk());

        // Delete member
        mockMvc.perform(delete("/api/v1/members/" + memberId))
                .andExpect(status().isNoContent());

        // Verify audit logs exist for all operations
        mockMvc.perform(get("/api/v1/audit/logs?entityType=Member&entityId=" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[?(@.operation=='CREATE')]").exists())
                .andExpect(jsonPath("$.content[?(@.operation=='UPDATE')]").exists())
                .andExpect(jsonPath("$.content[?(@.operation=='DELETE')]").exists());
    }

    @Test
    @DisplayName("E2E: Required field completeness in API responses")
    void testRequiredFieldCompleteness() throws Exception {
        // Create district
        District district = new District();
        district.setName("Adilabad");
        district.setCode("ADB");
        district.setHeadquarters("Adilabad");
        district.setArea(350.0);
        district.setPopulation(2000000L);
        district = districtRepository.save(district);

        // Create player
        Player player = new Player();
        player.setName("Complete Fields Player");
        player.setDateOfBirth(LocalDate.of(1995, 1, 1));
        player.setDistrict(district);
        player.setCategory("Men");
        player.setIsProminent(false);
        player = playerRepository.save(player);

        // Verify all required fields are present
        mockMvc.perform(get("/api/v1/players/" + player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.district").exists())
                .andExpect(jsonPath("$.district.id").exists())
                .andExpect(jsonPath("$.district.name").exists())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.isProminent").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("E2E: Input validation consistency across endpoints")
    void testInputValidationConsistency() throws Exception {
        // Test invalid email format
        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"position\":\"Test\",\"email\":\"invalid\",\"phone\":\"+91-1234567890\",\"biography\":\"Bio\",\"hierarchy\":1,\"tenureStartDate\":\"2023-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());

        // Test missing required field
        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"+91-1234567890\",\"biography\":\"Bio\",\"hierarchy\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Test invalid date format
        mockMvc.perform(post("/api/v1/districts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"TST\",\"headquarters\":\"Test\",\"area\":\"invalid\",\"population\":1000000}"))
                .andExpect(status().isBadRequest());
    }

    // Helper methods
    private void testCrudOperations(String endpoint, String createJson, String updateJson) throws Exception {
        // Create
        MvcResult createResult = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String id = extractIdFromResponse(createResult);

        // Read
        mockMvc.perform(get(endpoint + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        // Update
        mockMvc.perform(put(endpoint + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        // Delete
        mockMvc.perform(delete(endpoint + "/" + id))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get(endpoint + "/" + id))
                .andExpect(status().isNotFound());
    }

    private String extractIdFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asText();
    }
}
