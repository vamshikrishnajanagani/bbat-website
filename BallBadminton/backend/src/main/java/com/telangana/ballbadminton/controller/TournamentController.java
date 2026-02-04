package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.tournament.*;
import com.telangana.ballbadminton.entity.Tournament;
import com.telangana.ballbadminton.entity.TournamentRegistration;
import com.telangana.ballbadminton.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Tournament Management
 * Provides CRUD operations for tournaments, registration, and bracket generation
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/tournaments")
@Tag(name = "Tournament Management", description = "APIs for managing tournaments, registrations, and brackets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TournamentController {

    private static final Logger logger = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // Tournament CRUD Operations

    @Operation(summary = "Get all tournaments", description = "Retrieve all tournaments in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        logger.debug("GET /api/v1/tournaments - Fetching all tournaments");
        List<TournamentResponse> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get tournaments with pagination", description = "Retrieve tournaments with pagination and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tournaments"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<TournamentResponse>> getTournaments(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDate") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("GET /api/v1/tournaments/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Page<TournamentResponse> tournaments = tournamentService.getTournaments(page, size, sortBy, sortDir);
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get tournament by ID", description = "Retrieve a specific tournament by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tournament"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournamentById(
            @Parameter(description = "Tournament ID") @PathVariable UUID id) {
        
        logger.debug("GET /api/v1/tournaments/{} - Fetching tournament by ID", id);
        
        Optional<TournamentResponse> tournament = tournamentService.getTournamentById(id);
        return tournament.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new tournament", description = "Create a new tournament in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tournament created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid tournament data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<TournamentResponse> createTournament(@Valid @RequestBody TournamentRequest request) {
        logger.debug("POST /api/v1/tournaments - Creating new tournament: {}", request.getName());
        
        try {
            TournamentResponse createdTournament = tournamentService.createTournament(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTournament);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create tournament: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update tournament", description = "Update an existing tournament's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tournament updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid tournament data"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<TournamentResponse> updateTournament(
            @Parameter(description = "Tournament ID") @PathVariable UUID id,
            @Valid @RequestBody TournamentRequest request) {
        
        logger.debug("PUT /api/v1/tournaments/{} - Updating tournament", id);
        
        try {
            TournamentResponse updatedTournament = tournamentService.updateTournament(id, request);
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update tournament {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete tournament", description = "Delete a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tournament deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTournament(@Parameter(description = "Tournament ID") @PathVariable UUID id) {
        logger.debug("DELETE /api/v1/tournaments/{} - Deleting tournament", id);
        
        try {
            tournamentService.deleteTournament(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete tournament {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Tournament Status Management

    @Operation(summary = "Update tournament status", description = "Update the status of a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tournament status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<TournamentResponse> updateTournamentStatus(
            @Parameter(description = "Tournament ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam Tournament.Status status) {
        
        logger.debug("PATCH /api/v1/tournaments/{}/status - Updating status to {}", id, status);
        
        try {
            TournamentResponse updatedTournament = tournamentService.updateTournamentStatus(id, status);
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update tournament status {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Tournament Registration

    @Operation(summary = "Register player for tournament", description = "Register a player for a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Player registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or registration not allowed"),
        @ApiResponse(responseCode = "404", description = "Tournament or player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{tournamentId}/registrations")
    public ResponseEntity<TournamentRegistrationResponse> registerPlayer(
            @Parameter(description = "Tournament ID") @PathVariable UUID tournamentId,
            @Valid @RequestBody TournamentRegistrationRequest request) {
        
        logger.debug("POST /api/v1/tournaments/{}/registrations - Registering player", tournamentId);
        
        try {
            TournamentRegistrationResponse registration = tournamentService.registerPlayer(tournamentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(registration);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to register player for tournament {}: {}", tournamentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get tournament registrations", description = "Retrieve all registrations for a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved registrations"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{tournamentId}/registrations")
    public ResponseEntity<List<TournamentRegistrationResponse>> getTournamentRegistrations(
            @Parameter(description = "Tournament ID") @PathVariable UUID tournamentId) {
        
        logger.debug("GET /api/v1/tournaments/{}/registrations - Fetching registrations", tournamentId);
        
        try {
            List<TournamentRegistrationResponse> registrations = tournamentService.getTournamentRegistrations(tournamentId);
            return ResponseEntity.ok(registrations);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to fetch registrations for tournament {}: {}", tournamentId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update registration status", description = "Update the status of a tournament registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Tournament or registration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{tournamentId}/registrations/{registrationId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<TournamentRegistrationResponse> updateRegistrationStatus(
            @Parameter(description = "Tournament ID") @PathVariable UUID tournamentId,
            @Parameter(description = "Registration ID") @PathVariable UUID registrationId,
            @Parameter(description = "New status") @RequestParam TournamentRegistration.RegistrationStatus status) {
        
        logger.debug("PATCH /api/v1/tournaments/{}/registrations/{}/status - Updating status to {}", 
                    tournamentId, registrationId, status);
        
        try {
            TournamentRegistrationResponse registration = tournamentService.updateRegistrationStatus(
                tournamentId, registrationId, status);
            return ResponseEntity.ok(registration);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update registration status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Tournament Bracket Generation

    @Operation(summary = "Generate tournament bracket", description = "Generate a tournament bracket based on registrations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bracket generated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot generate bracket (no registrations or invalid state)"),
        @ApiResponse(responseCode = "404", description = "Tournament not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{tournamentId}/bracket")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<BracketResponse> generateBracket(
            @Parameter(description = "Tournament ID") @PathVariable UUID tournamentId) {
        
        logger.debug("POST /api/v1/tournaments/{}/bracket - Generating bracket", tournamentId);
        
        try {
            BracketResponse bracket = tournamentService.generateBracket(tournamentId);
            return ResponseEntity.ok(bracket);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to generate bracket for tournament {}: {}", tournamentId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            logger.warn("Cannot generate bracket for tournament {}: {}", tournamentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Tournament Filtering and Search

    @Operation(summary = "Get upcoming tournaments", description = "Retrieve all upcoming tournaments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved upcoming tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<TournamentResponse>> getUpcomingTournaments() {
        logger.debug("GET /api/v1/tournaments/upcoming - Fetching upcoming tournaments");
        List<TournamentResponse> tournaments = tournamentService.getUpcomingTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get ongoing tournaments", description = "Retrieve all ongoing tournaments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ongoing tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/ongoing")
    public ResponseEntity<List<TournamentResponse>> getOngoingTournaments() {
        logger.debug("GET /api/v1/tournaments/ongoing - Fetching ongoing tournaments");
        List<TournamentResponse> tournaments = tournamentService.getOngoingTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get completed tournaments", description = "Retrieve all completed tournaments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved completed tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/completed")
    public ResponseEntity<List<TournamentResponse>> getCompletedTournaments() {
        logger.debug("GET /api/v1/tournaments/completed - Fetching completed tournaments");
        List<TournamentResponse> tournaments = tournamentService.getCompletedTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get featured tournaments", description = "Retrieve all featured tournaments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved featured tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/featured")
    public ResponseEntity<List<TournamentResponse>> getFeaturedTournaments() {
        logger.debug("GET /api/v1/tournaments/featured - Fetching featured tournaments");
        List<TournamentResponse> tournaments = tournamentService.getFeaturedTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get tournaments by district", description = "Retrieve tournaments filtered by district")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tournaments"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<TournamentResponse>> getTournamentsByDistrict(
            @Parameter(description = "District ID") @PathVariable UUID districtId) {
        
        logger.debug("GET /api/v1/tournaments/district/{} - Fetching tournaments by district", districtId);
        List<TournamentResponse> tournaments = tournamentService.getTournamentsByDistrict(districtId);
        return ResponseEntity.ok(tournaments);
    }

    @Operation(summary = "Get tournaments by date range", description = "Retrieve tournaments within a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tournaments"),
        @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<TournamentResponse>> getTournamentsByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.debug("GET /api/v1/tournaments/date-range - Fetching tournaments between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<TournamentResponse> tournaments = tournamentService.getTournamentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(tournaments);
    }
}
