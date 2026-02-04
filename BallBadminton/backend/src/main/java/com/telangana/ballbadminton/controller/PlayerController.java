package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.player.AchievementRequest;
import com.telangana.ballbadminton.dto.player.AchievementResponse;
import com.telangana.ballbadminton.dto.player.PlayerRequest;
import com.telangana.ballbadminton.dto.player.PlayerResponse;
import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.service.PlayerService;
//import com.telangana.ballbadminton.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Player Management
 * Provides CRUD operations for players, achievements, and statistics
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Player Management", description = "APIs for managing players, achievements, and statistics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);


       private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Player CRUD Operations

    @Operation(summary = "Get all active players", description = "Retrieve all active players in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllActivePlayers() {
        logger.debug("GET /api/v1/players - Fetching all active players");
        List<PlayerResponse> players = playerService.getAllActivePlayers();
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get active players with pagination", description = "Retrieve active players with pagination and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<PlayerResponse>> getActivePlayers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("GET /api/v1/players/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Page<PlayerResponse> players = playerService.getActivePlayers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get player by ID", description = "Retrieve a specific player by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved player"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(
            @Parameter(description = "Player ID") @PathVariable UUID id) {
        
        logger.debug("GET /api/v1/players/{} - Fetching player by ID", id);
        
        Optional<PlayerResponse> player = playerService.getPlayerById(id);
        return player.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new player", description = "Create a new player in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Player created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid player data"),
        @ApiResponse(responseCode = "409", description = "Player with email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        logger.debug("POST /api/v1/players - Creating new player: {}", request.getName());
        
        try {
            PlayerResponse createdPlayer = playerService.createPlayer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create player: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update player", description = "Update an existing player's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid player data"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @Parameter(description = "Player ID") @PathVariable UUID id,
            @Valid @RequestBody PlayerRequest request) {
        
        logger.debug("PUT /api/v1/players/{} - Updating player", id);
        
        try {
            PlayerResponse updatedPlayer = playerService.updatePlayer(id, request);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update player {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete player", description = "Soft delete a player (set as inactive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Player deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlayer(@Parameter(description = "Player ID") @PathVariable UUID id) {
        logger.debug("DELETE /api/v1/players/{} - Deleting player", id);
        
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete player {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Player Search and Filtering

    @Operation(summary = "Get prominent players", description = "Retrieve all prominent players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved prominent players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/prominent")
    public ResponseEntity<List<PlayerResponse>> getProminentPlayers() {
        logger.debug("GET /api/v1/players/prominent - Fetching prominent players");
        List<PlayerResponse> players = playerService.getProminentPlayers();
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get players by category", description = "Retrieve players filtered by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "400", description = "Invalid category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByCategory(
            @Parameter(description = "Player category") @PathVariable Player.Category category) {
        
        logger.debug("GET /api/v1/players/category/{} - Fetching players by category", category);
        List<PlayerResponse> players = playerService.getPlayersByCategory(category);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get players by district", description = "Retrieve players filtered by district")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByDistrict(
            @Parameter(description = "District ID") @PathVariable UUID districtId) {
        
        logger.debug("GET /api/v1/players/district/{} - Fetching players by district", districtId);
        List<PlayerResponse> players = playerService.getPlayersByDistrict(districtId);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Search players", description = "Search players by name, email, or phone")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<PlayerResponse>> searchPlayers(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("GET /api/v1/players/search - Searching players with term: {}", q);
        
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<PlayerResponse> players = playerService.searchPlayers(q.trim(), page, size);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Filter players", description = "Filter players with advanced criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered players"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/filter")
    public ResponseEntity<Page<PlayerResponse>> filterPlayers(
            @Parameter(description = "Player category") @RequestParam(required = false) Player.Category category,
            @Parameter(description = "Player gender") @RequestParam(required = false) Player.Gender gender,
            @Parameter(description = "District ID") @RequestParam(required = false) UUID districtId,
            @Parameter(description = "Is prominent player") @RequestParam(required = false) Boolean isProminent,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("GET /api/v1/players/filter - Filtering players with criteria");
        
        Page<PlayerResponse> players = playerService.filterPlayers(category, gender, districtId, isProminent, 
                                                                  page, size, sortBy, sortDir);
        return ResponseEntity.ok(players);
    }

    // Player Rankings and Statistics

    @Operation(summary = "Get top ranked players", description = "Retrieve top ranked players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved top ranked players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rankings/top")
    public ResponseEntity<List<PlayerResponse>> getTopRankedPlayers(
            @Parameter(description = "Number of players to retrieve") @RequestParam(defaultValue = "10") int limit) {
        
        logger.debug("GET /api/v1/players/rankings/top - Fetching top {} ranked players", limit);
        List<PlayerResponse> players = playerService.getTopRankedPlayers(limit);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get players with most tournament wins", description = "Retrieve players with most tournament wins")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics/tournament-winners")
    public ResponseEntity<List<PlayerResponse>> getPlayersWithMostTournamentWins(
            @Parameter(description = "Number of players to retrieve") @RequestParam(defaultValue = "10") int limit) {
        
        logger.debug("GET /api/v1/players/statistics/tournament-winners - Fetching players with most tournament wins");
        List<PlayerResponse> players = playerService.getPlayersWithMostTournamentWins(limit);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get players with highest win percentage", description = "Retrieve players with highest win percentage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved players"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics/win-percentage")
    public ResponseEntity<List<PlayerResponse>> getPlayersWithHighestWinPercentage(
            @Parameter(description = "Number of players to retrieve") @RequestParam(defaultValue = "10") int limit) {
        
        logger.debug("GET /api/v1/players/statistics/win-percentage - Fetching players with highest win percentage");
        List<PlayerResponse> players = playerService.getPlayersWithHighestWinPercentage(limit);
        return ResponseEntity.ok(players);
    }

    @Operation(summary = "Get player statistics summary", description = "Retrieve overall player statistics summary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics summary"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics/summary")
    public ResponseEntity<PlayerService.PlayerStatisticsSummary> getPlayerStatisticsSummary() {
        logger.debug("GET /api/v1/players/statistics/summary - Fetching player statistics summary");
        PlayerService.PlayerStatisticsSummary summary = playerService.getPlayerStatisticsSummary();
        return ResponseEntity.ok(summary);
    }

    // Player Achievement Management

    @Operation(summary = "Get player achievements", description = "Retrieve all achievements for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved achievements"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{playerId}/achievements")
    public ResponseEntity<List<AchievementResponse>> getPlayerAchievements(
            @Parameter(description = "Player ID") @PathVariable UUID playerId) {
        
        logger.debug("GET /api/v1/players/{}/achievements - Fetching player achievements", playerId);
        List<AchievementResponse> achievements = playerService.getPlayerAchievements(playerId);
        return ResponseEntity.ok(achievements);
    }

    @Operation(summary = "Get player achievements with pagination", description = "Retrieve player achievements with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved achievements"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{playerId}/achievements/paginated")
    public ResponseEntity<Page<AchievementResponse>> getPlayerAchievementsPaginated(
            @Parameter(description = "Player ID") @PathVariable UUID playerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("GET /api/v1/players/{}/achievements/paginated - Fetching player achievements with pagination", playerId);
        Page<AchievementResponse> achievements = playerService.getPlayerAchievements(playerId, page, size);
        return ResponseEntity.ok(achievements);
    }

    @Operation(summary = "Add player achievement", description = "Add a new achievement to a player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Achievement added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid achievement data"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{playerId}/achievements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AchievementResponse> addPlayerAchievement(
            @Parameter(description = "Player ID") @PathVariable UUID playerId,
            @Valid @RequestBody AchievementRequest request) {
        
        logger.debug("POST /api/v1/players/{}/achievements - Adding achievement to player", playerId);
        
        try {
            AchievementResponse achievement = playerService.addPlayerAchievement(playerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(achievement);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to add achievement to player {}: {}", playerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update player achievement", description = "Update an existing player achievement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Achievement updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid achievement data"),
        @ApiResponse(responseCode = "404", description = "Player or achievement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{playerId}/achievements/{achievementId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AchievementResponse> updatePlayerAchievement(
            @Parameter(description = "Player ID") @PathVariable UUID playerId,
            @Parameter(description = "Achievement ID") @PathVariable UUID achievementId,
            @Valid @RequestBody AchievementRequest request) {
        
        logger.debug("PUT /api/v1/players/{}/achievements/{} - Updating player achievement", playerId, achievementId);
        
        try {
            AchievementResponse achievement = playerService.updatePlayerAchievement(playerId, achievementId, request);
            return ResponseEntity.ok(achievement);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update achievement {} for player {}: {}", achievementId, playerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete player achievement", description = "Delete a player achievement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Achievement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Player or achievement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{playerId}/achievements/{achievementId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deletePlayerAchievement(
            @Parameter(description = "Player ID") @PathVariable UUID playerId,
            @Parameter(description = "Achievement ID") @PathVariable UUID achievementId) {
        
        logger.debug("DELETE /api/v1/players/{}/achievements/{} - Deleting player achievement", playerId, achievementId);
        
        try {
            playerService.deletePlayerAchievement(playerId, achievementId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete achievement {} for player {}: {}", achievementId, playerId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Player Statistics Management

    @Operation(summary = "Update player statistics", description = "Update player statistics and performance metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid statistics data"),
        @ApiResponse(responseCode = "404", description = "Player not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{playerId}/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<PlayerResponse> updatePlayerStatistics(
            @Parameter(description = "Player ID") @PathVariable UUID playerId,
            @Valid @RequestBody PlayerService.PlayerStatisticsRequest request) {
        
        logger.debug("PUT /api/v1/players/{}/statistics - Updating player statistics", playerId);
        
        try {
            PlayerResponse player = playerService.updatePlayerStatistics(playerId, request);
            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update statistics for player {}: {}", playerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calculate player rankings", description = "Recalculate and update player rankings based on current statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rankings calculated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/rankings/calculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> calculatePlayerRankings() {
        logger.debug("POST /api/v1/players/rankings/calculate - Calculating player rankings");
        
        try {
            playerService.calculatePlayerRankings();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to calculate player rankings: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}