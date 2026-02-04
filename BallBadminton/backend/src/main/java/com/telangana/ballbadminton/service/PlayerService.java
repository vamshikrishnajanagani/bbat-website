package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.player.AchievementRequest;
import com.telangana.ballbadminton.dto.player.AchievementResponse;
import com.telangana.ballbadminton.dto.player.PlayerRequest;
import com.telangana.ballbadminton.dto.player.PlayerResponse;
import com.telangana.ballbadminton.entity.*;
import com.telangana.ballbadminton.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for player management operations
 * Handles business logic for player CRUD operations, achievement tracking, and statistics
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
@Transactional
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final AchievementRepository achievementRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final DistrictRepository districtRepository;
    private final TournamentRepository tournamentRepository;

    public PlayerService(PlayerRepository playerRepository,
                        AchievementRepository achievementRepository,
                        PlayerStatisticsRepository playerStatisticsRepository,
                        DistrictRepository districtRepository,
                        TournamentRepository tournamentRepository) {
        this.playerRepository = playerRepository;
        this.achievementRepository = achievementRepository;
        this.playerStatisticsRepository = playerStatisticsRepository;
        this.districtRepository = districtRepository;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * Get all active players
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllActivePlayers() {
        logger.debug("Fetching all active players");
        List<Player> players = playerRepository.findByIsActiveTrueOrderByNameAsc();
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get active players with pagination
     */
    @Transactional(readOnly = true)
    public Page<PlayerResponse> getActivePlayers(int page, int size, String sortBy, String sortDir) {
        logger.debug("Fetching active players - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Player> playerPage = playerRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return playerPage.map(PlayerResponse::new);
    }

    /**
     * Get player by ID
     */
    @Cacheable(value = "players", key = "#id")
    @Transactional(readOnly = true)
    public Optional<PlayerResponse> getPlayerById(UUID id) {
        logger.debug("Fetching player by ID: {}", id);
        return playerRepository.findById(id)
                .map(PlayerResponse::new);
    }

    /**
     * Get prominent players
     */
    @Cacheable(value = "players", key = "'prominent'")
    @Transactional(readOnly = true)
    public List<PlayerResponse> getProminentPlayers() {
        logger.debug("Fetching prominent players");
        List<Player> players = playerRepository.findByIsActiveTrueAndIsProminentTrueOrderByNameAsc();
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get players by category
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByCategory(Player.Category category) {
        logger.debug("Fetching players by category: {}", category);
        List<Player> players = playerRepository.findByIsActiveTrueAndCategoryOrderByNameAsc(category);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get players by district
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByDistrict(UUID districtId) {
        logger.debug("Fetching players by district: {}", districtId);
        List<Player> players = playerRepository.findByIsActiveTrueAndDistrictIdOrderByNameAsc(districtId);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search players
     */
    @Transactional(readOnly = true)
    public Page<PlayerResponse> searchPlayers(String searchTerm, int page, int size) {
        logger.debug("Searching players with term: {}", searchTerm);
        Pageable pageable = PageRequest.of(page, size);
        Page<Player> playerPage = playerRepository.searchPlayers(searchTerm, pageable);
        return playerPage.map(PlayerResponse::new);
    }

    /**
     * Filter players with advanced criteria
     */
    @Transactional(readOnly = true)
    public Page<PlayerResponse> filterPlayers(Player.Category category, Player.Gender gender, 
                                            UUID districtId, Boolean isProminent, 
                                            int page, int size, String sortBy, String sortDir) {
        logger.debug("Filtering players with criteria - category: {}, gender: {}, district: {}, prominent: {}", 
                    category, gender, districtId, isProminent);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Player> playerPage = playerRepository.findPlayersWithFilters(category, gender, districtId, isProminent, pageable);
        return playerPage.map(PlayerResponse::new);
    }

    /**
     * Create a new player
     */
    @Caching(evict = {
        @CacheEvict(value = "players", key = "'prominent'", condition = "#result.isProminent"),
        @CacheEvict(value = "statistics", allEntries = true)
    })
    public PlayerResponse createPlayer(PlayerRequest request) {
        logger.debug("Creating new player: {}", request.getName());
        
        // Validate email uniqueness
        if (request.getContactEmail() != null && playerRepository.existsByContactEmail(request.getContactEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getContactEmail());
        }
        
        Player player = new Player();
        mapRequestToEntity(request, player);
        
        // Set district if provided
        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new IllegalArgumentException("District not found: " + request.getDistrictId()));
            player.setDistrict(district);
        }
        
        Player savedPlayer = playerRepository.save(player);
        
        // Create initial statistics
        PlayerStatistics statistics = new PlayerStatistics(savedPlayer);
        playerStatisticsRepository.save(statistics);
        savedPlayer.setStatistics(statistics);
        
        logger.info("Created player with ID: {}", savedPlayer.getId());
        return new PlayerResponse(savedPlayer);
    }

    /**
     * Update an existing player
     */
    @Caching(evict = {
        @CacheEvict(value = "players", key = "#id"),
        @CacheEvict(value = "players", key = "'prominent'"),
        @CacheEvict(value = "statistics", allEntries = true),
        @CacheEvict(value = "rankings", allEntries = true)
    })
    public PlayerResponse updatePlayer(UUID id, PlayerRequest request) {
        logger.debug("Updating player: {}", id);
        
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + id));
        
        // Validate email uniqueness (excluding current player)
        if (request.getContactEmail() != null && 
            playerRepository.existsByContactEmailAndIdNot(request.getContactEmail(), id)) {
            throw new IllegalArgumentException("Email already exists: " + request.getContactEmail());
        }
        
        mapRequestToEntity(request, player);
        
        // Update district if provided
        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new IllegalArgumentException("District not found: " + request.getDistrictId()));
            player.setDistrict(district);
        } else {
            player.setDistrict(null);
        }
        
        Player savedPlayer = playerRepository.save(player);
        logger.info("Updated player with ID: {}", savedPlayer.getId());
        return new PlayerResponse(savedPlayer);
    }

    /**
     * Delete a player (soft delete by setting isActive to false)
     */
    @Caching(evict = {
        @CacheEvict(value = "players", key = "#id"),
        @CacheEvict(value = "players", key = "'prominent'"),
        @CacheEvict(value = "statistics", allEntries = true),
        @CacheEvict(value = "rankings", allEntries = true)
    })
    public void deletePlayer(UUID id) {
        logger.debug("Deleting player: {}", id);
        
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + id));
        
        player.setIsActive(false);
        playerRepository.save(player);
        
        logger.info("Deleted player with ID: {}", id);
    }

    /**
     * Get top ranked players
     */
    @Cacheable(value = "rankings", key = "'top-' + #limit")
    @Transactional(readOnly = true)
    public List<PlayerResponse> getTopRankedPlayers(int limit) {
        logger.debug("Fetching top {} ranked players", limit);
        Pageable pageable = PageRequest.of(0, limit);
        List<Player> players = playerRepository.findTopRankedPlayers(pageable);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get players with most tournament wins
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersWithMostTournamentWins(int limit) {
        logger.debug("Fetching players with most tournament wins, limit: {}", limit);
        Pageable pageable = PageRequest.of(0, limit);
        List<Player> players = playerRepository.findPlayersWithMostTournamentWins(pageable);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get players with highest win percentage
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersWithHighestWinPercentage(int limit) {
        logger.debug("Fetching players with highest win percentage, limit: {}", limit);
        Pageable pageable = PageRequest.of(0, limit);
        List<Player> players = playerRepository.findPlayersWithHighestWinPercentage(pageable);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get player statistics summary
     */
    @Transactional(readOnly = true)
    public PlayerStatisticsSummary getPlayerStatisticsSummary() {
        logger.debug("Fetching player statistics summary");
        
        long totalPlayers = playerRepository.countByIsActiveTrue();
        long prominentPlayers = playerRepository.countByIsActiveTrueAndIsProminentTrue();
        
        // Get category-wise counts
        List<Object[]> categoryStats = playerRepository.getCategoryWisePlayerCounts();
        
        // Get district-wise counts
        List<Object[]> districtStats = playerRepository.getDistrictWisePlayerCounts();
        
        return new PlayerStatisticsSummary(totalPlayers, prominentPlayers, categoryStats, districtStats);
    }

    // Achievement Management Methods

    /**
     * Add achievement to player
     */
    public AchievementResponse addPlayerAchievement(UUID playerId, AchievementRequest request) {
        logger.debug("Adding achievement to player: {}", playerId);
        
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        
        Achievement achievement = new Achievement();
        mapAchievementRequestToEntity(request, achievement);
        achievement.setPlayer(player);
        
        // Set tournament if provided
        if (request.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + request.getTournamentId()));
            achievement.setTournament(tournament);
        }
        
        Achievement savedAchievement = achievementRepository.save(achievement);
        logger.info("Added achievement with ID: {} to player: {}", savedAchievement.getId(), playerId);
        
        return new AchievementResponse(savedAchievement);
    }

    /**
     * Update player achievement
     */
    public AchievementResponse updatePlayerAchievement(UUID playerId, UUID achievementId, AchievementRequest request) {
        logger.debug("Updating achievement: {} for player: {}", achievementId, playerId);
        
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + achievementId));
        
        if (!achievement.getPlayer().getId().equals(playerId)) {
            throw new IllegalArgumentException("Achievement does not belong to player: " + playerId);
        }
        
        mapAchievementRequestToEntity(request, achievement);
        
        // Update tournament if provided
        if (request.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + request.getTournamentId()));
            achievement.setTournament(tournament);
        } else {
            achievement.setTournament(null);
        }
        
        Achievement savedAchievement = achievementRepository.save(achievement);
        logger.info("Updated achievement with ID: {}", savedAchievement.getId());
        
        return new AchievementResponse(savedAchievement);
    }

    /**
     * Delete player achievement
     */
    public void deletePlayerAchievement(UUID playerId, UUID achievementId) {
        logger.debug("Deleting achievement: {} for player: {}", achievementId, playerId);
        
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + achievementId));
        
        if (!achievement.getPlayer().getId().equals(playerId)) {
            throw new IllegalArgumentException("Achievement does not belong to player: " + playerId);
        }
        
        achievementRepository.delete(achievement);
        logger.info("Deleted achievement with ID: {}", achievementId);
    }

    /**
     * Get player achievements
     */
    @Transactional(readOnly = true)
    public List<AchievementResponse> getPlayerAchievements(UUID playerId) {
        logger.debug("Fetching achievements for player: {}", playerId);
        
        List<Achievement> achievements = achievementRepository.findByPlayerIdOrderByAchievementDateDesc(playerId);
        return achievements.stream()
                .map(AchievementResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get player achievements with pagination
     */
    @Transactional(readOnly = true)
    public Page<AchievementResponse> getPlayerAchievements(UUID playerId, int page, int size) {
        logger.debug("Fetching achievements for player: {} with pagination", playerId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Achievement> achievementPage = achievementRepository.findByPlayerIdOrderByAchievementDateDesc(playerId, pageable);
        return achievementPage.map(AchievementResponse::new);
    }

    // Statistics Management Methods

    /**
     * Update player statistics
     */
    public PlayerResponse updatePlayerStatistics(UUID playerId, PlayerStatisticsRequest request) {
        logger.debug("Updating statistics for player: {}", playerId);
        
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        
        PlayerStatistics statistics = player.getStatistics();
        if (statistics == null) {
            statistics = new PlayerStatistics(player);
        }
        
        // Update statistics fields
        if (request.getMatchesPlayed() != null) {
            statistics.setMatchesPlayed(request.getMatchesPlayed());
        }
        if (request.getMatchesWon() != null) {
            statistics.setMatchesWon(request.getMatchesWon());
        }
        if (request.getTournamentsParticipated() != null) {
            statistics.setTournamentsParticipated(request.getTournamentsParticipated());
        }
        if (request.getTournamentsWon() != null) {
            statistics.setTournamentsWon(request.getTournamentsWon());
        }
        if (request.getCurrentRanking() != null) {
            statistics.setCurrentRanking(request.getCurrentRanking());
        }
        if (request.getTotalPoints() != null) {
            statistics.setTotalPoints(request.getTotalPoints());
        }
        
        playerStatisticsRepository.save(statistics);
        player.setStatistics(statistics);
        
        logger.info("Updated statistics for player: {}", playerId);
        return new PlayerResponse(player);
    }

    /**
     * Calculate and update player rankings
     */
    public void calculatePlayerRankings() {
        logger.debug("Calculating player rankings");
        
        List<PlayerStatistics> playersForRanking = playerStatisticsRepository.findPlayersForRankingCalculation();
        
        int ranking = 1;
        for (PlayerStatistics stats : playersForRanking) {
            stats.setCurrentRanking(ranking++);
            playerStatisticsRepository.save(stats);
        }
        
        logger.info("Updated rankings for {} players", playersForRanking.size());
    }

    // Helper Methods

    private void mapRequestToEntity(PlayerRequest request, Player player) {
        player.setName(request.getName());
        player.setDateOfBirth(request.getDateOfBirth());
        player.setGender(request.getGender());
        player.setCategory(request.getCategory());
        player.setProfilePhotoUrl(request.getProfilePhotoUrl());
        player.setContactEmail(request.getContactEmail());
        player.setContactPhone(request.getContactPhone());
        player.setAddress(request.getAddress());
        player.setIsProminent(request.getIsProminent());
        player.setIsActive(request.getIsActive());
    }

    private void mapAchievementRequestToEntity(AchievementRequest request, Achievement achievement) {
        achievement.setTitle(request.getTitle());
        achievement.setDescription(request.getDescription());
        achievement.setAchievementDate(request.getAchievementDate());
        achievement.setCategory(request.getCategory());
        achievement.setLevel(request.getLevel());
        achievement.setPosition(request.getPosition());
        achievement.setIsVerified(request.getIsVerified());
    }

    // Inner class for statistics summary
    public static class PlayerStatisticsSummary {
        private final long totalPlayers;
        private final long prominentPlayers;
        private final List<Object[]> categoryStats;
        private final List<Object[]> districtStats;

        public PlayerStatisticsSummary(long totalPlayers, long prominentPlayers, 
                                     List<Object[]> categoryStats, List<Object[]> districtStats) {
            this.totalPlayers = totalPlayers;
            this.prominentPlayers = prominentPlayers;
            this.categoryStats = categoryStats;
            this.districtStats = districtStats;
        }

        public long getTotalPlayers() { return totalPlayers; }
        public long getProminentPlayers() { return prominentPlayers; }
        public List<Object[]> getCategoryStats() { return categoryStats; }
        public List<Object[]> getDistrictStats() { return districtStats; }
    }

    // Inner class for statistics request
    public static class PlayerStatisticsRequest {
        private Integer matchesPlayed;
        private Integer matchesWon;
        private Integer tournamentsParticipated;
        private Integer tournamentsWon;
        private Integer currentRanking;
        private Integer totalPoints;

        // Getters and setters
        public Integer getMatchesPlayed() { return matchesPlayed; }
        public void setMatchesPlayed(Integer matchesPlayed) { this.matchesPlayed = matchesPlayed; }

        public Integer getMatchesWon() { return matchesWon; }
        public void setMatchesWon(Integer matchesWon) { this.matchesWon = matchesWon; }

        public Integer getTournamentsParticipated() { return tournamentsParticipated; }
        public void setTournamentsParticipated(Integer tournamentsParticipated) { this.tournamentsParticipated = tournamentsParticipated; }

        public Integer getTournamentsWon() { return tournamentsWon; }
        public void setTournamentsWon(Integer tournamentsWon) { this.tournamentsWon = tournamentsWon; }

        public Integer getCurrentRanking() { return currentRanking; }
        public void setCurrentRanking(Integer currentRanking) { this.currentRanking = currentRanking; }

        public Integer getTotalPoints() { return totalPoints; }
        public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    }
}