package com.telangana.ballbadminton.e2e;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.dto.tournament.CreateTournamentRequest;
import com.telangana.ballbadminton.dto.tournament.TournamentRegistrationRequest;
import com.telangana.ballbadminton.dto.tournament.UpdateTournamentRequest;
import com.telangana.ballbadminton.entity.District;
import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.entity.Tournament;
import com.telangana.ballbadminton.repository.DistrictRepository;
import com.telangana.ballbadminton.repository.PlayerRepository;
import com.telangana.ballbadminton.repository.TournamentRegistrationRepository;
import com.telangana.ballbadminton.repository.TournamentRepository;
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
 * End-to-End tests for Tournament Management functionality
 * 
 * Tests critical user journeys:
 * - Creating tournaments
 * - Tournament registration
 * - Status updates and notifications
 * - Results tracking
 * - Tournament search and filtering
 * 
 * Validates Requirements: 4.1, 4.2, 4.3, 4.4, 4.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Tournament Management E2E Tests")
class TournamentManagementE2ETest extends BaseIntegrationTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private DistrictRepository districtRepository;

    private District testDistrict;
    private Player testPlayer;

    @BeforeEach
    void setupTestData() {
        registrationRepository.deleteAll();
        tournamentRepository.deleteAll();
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

        // Create test player
        testPlayer = new Player();
        testPlayer.setName("Test Player");
        testPlayer.setDateOfBirth(LocalDate.of(1995, 1, 1));
        testPlayer.setDistrict(testDistrict);
        testPlayer.setCategory("Men");
        testPlayer.setIsProminent(false);
        testPlayer = playerRepository.save(testPlayer);
    }

    @Test
    @DisplayName("E2E: Complete tournament lifecycle from creation to completion")
    void testCompleteTournamentLifecycle() throws Exception {
        // Step 1: Create tournament
        CreateTournamentRequest createRequest = new CreateTournamentRequest();
        createRequest.setName("State Championship 2024");
        createRequest.setDescription("Annual state level championship");
        createRequest.setStartDate(LocalDate.now().plusMonths(2));
        createRequest.setEndDate(LocalDate.now().plusMonths(2).plusDays(3));
        createRequest.setVenue("Gachibowli Indoor Stadium");
        createRequest.setDistrictId(testDistrict.getId());
        createRequest.setRegistrationDeadline(LocalDate.now().plusMonths(1));
        createRequest.setMaxParticipants(64);
        createRequest.setEntryFee(500.0);

        MvcResult createResult = mockMvc.perform(post("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("State Championship 2024"))
                .andExpect(jsonPath("$.status").value("Upcoming"))
                .andReturn();

        String tournamentId = extractIdFromResponse(createResult);

        // Step 2: Register player for tournament
        TournamentRegistrationRequest registrationRequest = new TournamentRegistrationRequest();
        registrationRequest.setPlayerId(testPlayer.getId());
        registrationRequest.setCategory("Men");

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value(tournamentId))
                .andExpect(jsonPath("$.playerId").value(testPlayer.getId().toString()))
                .andExpect(jsonPath("$.status").value("Registered"));

        // Step 3: Verify registration is stored
        mockMvc.perform(get("/api/v1/tournaments/" + tournamentId + "/registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        // Step 4: Update tournament status
        UpdateTournamentRequest updateRequest = new UpdateTournamentRequest();
        updateRequest.setStatus("Ongoing");

        mockMvc.perform(put("/api/v1/tournaments/" + tournamentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Ongoing"));

        // Step 5: Add tournament results
        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":1,\"playerId\":\"" + testPlayer.getId() + "\",\"category\":\"Men\"}"))
                .andExpect(status().isCreated());

        // Step 6: Complete tournament
        UpdateTournamentRequest completeRequest = new UpdateTournamentRequest();
        completeRequest.setStatus("Completed");

        mockMvc.perform(put("/api/v1/tournaments/" + tournamentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(completeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Completed"));

        // Step 7: Verify tournament data integrity
        mockMvc.perform(get("/api/v1/tournaments/" + tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("State Championship 2024"))
                .andExpect(jsonPath("$.status").value("Completed"))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    @DisplayName("E2E: Tournament registration validation")
    void testTournamentRegistrationValidation() throws Exception {
        // Create tournament with past registration deadline
        String tournamentId = createTournament("Past Deadline Tournament", 
                LocalDate.now().minusDays(1), 
                LocalDate.now().plusMonths(1));

        // Try to register after deadline
        TournamentRegistrationRequest registrationRequest = new TournamentRegistrationRequest();
        registrationRequest.setPlayerId(testPlayer.getId());
        registrationRequest.setCategory("Men");

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration deadline has passed"));
    }

    @Test
    @DisplayName("E2E: Tournament capacity management")
    void testTournamentCapacityManagement() throws Exception {
        // Create tournament with limited capacity
        CreateTournamentRequest createRequest = new CreateTournamentRequest();
        createRequest.setName("Limited Capacity Tournament");
        createRequest.setDescription("Test tournament");
        createRequest.setStartDate(LocalDate.now().plusMonths(1));
        createRequest.setEndDate(LocalDate.now().plusMonths(1).plusDays(2));
        createRequest.setVenue("Test Venue");
        createRequest.setDistrictId(testDistrict.getId());
        createRequest.setRegistrationDeadline(LocalDate.now().plusDays(15));
        createRequest.setMaxParticipants(2);
        createRequest.setEntryFee(500.0);

        MvcResult createResult = mockMvc.perform(post("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String tournamentId = extractIdFromResponse(createResult);

        // Register first player
        TournamentRegistrationRequest reg1 = new TournamentRegistrationRequest();
        reg1.setPlayerId(testPlayer.getId());
        reg1.setCategory("Men");

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reg1)))
                .andExpect(status().isCreated());

        // Create and register second player
        Player player2 = new Player();
        player2.setName("Player 2");
        player2.setDateOfBirth(LocalDate.of(1996, 1, 1));
        player2.setDistrict(testDistrict);
        player2.setCategory("Men");
        player2.setIsProminent(false);
        player2 = playerRepository.save(player2);

        TournamentRegistrationRequest reg2 = new TournamentRegistrationRequest();
        reg2.setPlayerId(player2.getId());
        reg2.setCategory("Men");

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reg2)))
                .andExpect(status().isCreated());

        // Try to register third player (should fail - capacity reached)
        Player player3 = new Player();
        player3.setName("Player 3");
        player3.setDateOfBirth(LocalDate.of(1997, 1, 1));
        player3.setDistrict(testDistrict);
        player3.setCategory("Men");
        player3.setIsProminent(false);
        player3 = playerRepository.save(player3);

        TournamentRegistrationRequest reg3 = new TournamentRegistrationRequest();
        reg3.setPlayerId(player3.getId());
        reg3.setCategory("Men");

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reg3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tournament has reached maximum capacity"));
    }

    @Test
    @DisplayName("E2E: Tournament search and filtering")
    void testTournamentSearchAndFiltering() throws Exception {
        // Create tournaments with different statuses and dates
        createTournamentWithStatus("Upcoming Tournament 1", "Upcoming", LocalDate.now().plusMonths(1));
        createTournamentWithStatus("Upcoming Tournament 2", "Upcoming", LocalDate.now().plusMonths(2));
        createTournamentWithStatus("Ongoing Tournament", "Ongoing", LocalDate.now());
        createTournamentWithStatus("Completed Tournament", "Completed", LocalDate.now().minusMonths(1));

        // Filter by status
        mockMvc.perform(get("/api/v1/tournaments?status=Upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        // Filter by date range
        mockMvc.perform(get("/api/v1/tournaments?startDate=" + LocalDate.now() + "&endDate=" + LocalDate.now().plusMonths(3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));

        // Filter by district
        mockMvc.perform(get("/api/v1/tournaments?districtId=" + testDistrict.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(4));
    }

    @Test
    @DisplayName("E2E: Tournament data consistency across updates")
    void testTournamentDataConsistency() throws Exception {
        String tournamentId = createTournament("Consistency Test Tournament", 
                LocalDate.now().plusDays(30), 
                LocalDate.now().plusMonths(2));

        // Perform multiple updates
        for (int i = 0; i < 5; i++) {
            UpdateTournamentRequest updateRequest = new UpdateTournamentRequest();
            updateRequest.setDescription("Updated description " + i);

            mockMvc.perform(put("/api/v1/tournaments/" + tournamentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateRequest)))
                    .andExpect(status().isOk());

            // Verify update is immediately reflected
            mockMvc.perform(get("/api/v1/tournaments/" + tournamentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description").value("Updated description " + i));
        }
    }

    @Test
    @DisplayName("E2E: Tournament association integrity")
    void testTournamentAssociationIntegrity() throws Exception {
        String tournamentId = createTournament("Association Test Tournament", 
                LocalDate.now().plusDays(30), 
                LocalDate.now().plusMonths(2));

        // Verify tournament is associated with correct district
        mockMvc.perform(get("/api/v1/tournaments/" + tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.district.id").value(testDistrict.getId().toString()))
                .andExpect(jsonPath("$.district.name").value("Hyderabad"));

        // Verify district shows tournament
        mockMvc.perform(get("/api/v1/districts/" + testDistrict.getId() + "/tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id=='" + tournamentId + "')]").exists());
    }

    // Helper methods
    private String createTournament(String name, LocalDate registrationDeadline, LocalDate startDate) throws Exception {
        CreateTournamentRequest request = new CreateTournamentRequest();
        request.setName(name);
        request.setDescription("Test tournament");
        request.setStartDate(startDate);
        request.setEndDate(startDate.plusDays(3));
        request.setVenue("Test Venue");
        request.setDistrictId(testDistrict.getId());
        request.setRegistrationDeadline(registrationDeadline);
        request.setMaxParticipants(32);
        request.setEntryFee(500.0);

        MvcResult result = mockMvc.perform(post("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return extractIdFromResponse(result);
    }

    private String createTournamentWithStatus(String name, String status, LocalDate startDate) throws Exception {
        String tournamentId = createTournament(name, LocalDate.now().plusDays(15), startDate);

        if (!status.equals("Upcoming")) {
            UpdateTournamentRequest updateRequest = new UpdateTournamentRequest();
            updateRequest.setStatus(status);

            mockMvc.perform(put("/api/v1/tournaments/" + tournamentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateRequest)))
                    .andExpect(status().isOk());
        }

        return tournamentId;
    }

    private String extractIdFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asText();
    }
}
