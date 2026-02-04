package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Player entity
 * Provides data access methods for player management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    /**
     * Find all active players
     */
    List<Player> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active players with pagination
     */
    Page<Player> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find prominent players
     */
    List<Player> findByIsActiveTrueAndIsProminentTrueOrderByNameAsc();

    /**
     * Find players by category
     */
    List<Player> findByIsActiveTrueAndCategoryOrderByNameAsc(Player.Category category);

    /**
     * Find players by gender
     */
    List<Player> findByIsActiveTrueAndGenderOrderByNameAsc(Player.Gender gender);

    /**
     * Find players by district
     */
    List<Player> findByIsActiveTrueAndDistrictIdOrderByNameAsc(UUID districtId);

    /**
     * Find players by district with pagination
     */
    Page<Player> findByIsActiveTrueAndDistrictIdOrderByNameAsc(UUID districtId, Pageable pageable);

    /**
     * Search players by name, email, or phone
     */
    @Query("SELECT p FROM Player p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.contactPhone LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Player> searchPlayers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find players with advanced filtering
     */
    @Query("SELECT p FROM Player p WHERE p.isActive = true " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:gender IS NULL OR p.gender = :gender) " +
           "AND (:districtId IS NULL OR p.district.id = :districtId) " +
           "AND (:isProminent IS NULL OR p.isProminent = :isProminent) " +
           "ORDER BY p.name ASC")
    Page<Player> findPlayersWithFilters(
            @Param("category") Player.Category category,
            @Param("gender") Player.Gender gender,
            @Param("districtId") UUID districtId,
            @Param("isProminent") Boolean isProminent,
            Pageable pageable);

    /**
     * Find top ranked players (those with current ranking)
     */
    @Query("SELECT p FROM Player p JOIN p.statistics s WHERE p.isActive = true " +
           "AND s.currentRanking IS NOT NULL ORDER BY s.currentRanking ASC")
    List<Player> findTopRankedPlayers(Pageable pageable);

    /**
     * Find players with most tournament wins
     */
    @Query("SELECT p FROM Player p JOIN p.statistics s WHERE p.isActive = true " +
           "AND s.tournamentsWon > 0 ORDER BY s.tournamentsWon DESC, s.tournamentsParticipated ASC")
    List<Player> findPlayersWithMostTournamentWins(Pageable pageable);

    /**
     * Find players with highest win percentage (minimum 10 matches)
     */
    @Query("SELECT p FROM Player p JOIN p.statistics s WHERE p.isActive = true " +
           "AND s.matchesPlayed >= 10 ORDER BY s.winPercentage DESC, s.matchesPlayed DESC")
    List<Player> findPlayersWithHighestWinPercentage(Pageable pageable);

    /**
     * Count active players
     */
    long countByIsActiveTrue();

    /**
     * Count prominent players
     */
    long countByIsActiveTrueAndIsProminentTrue();

    /**
     * Count players by category
     */
    long countByIsActiveTrueAndCategory(Player.Category category);

    /**
     * Count players by gender
     */
    long countByIsActiveTrueAndGender(Player.Gender gender);

    /**
     * Count players by district
     */
    long countByIsActiveTrueAndDistrictId(UUID districtId);

    /**
     * Check if email exists
     */
    boolean existsByContactEmail(String email);

    /**
     * Check if email exists excluding specific player
     */
    @Query("SELECT COUNT(p) > 0 FROM Player p WHERE p.contactEmail = :email AND p.id != :playerId")
    boolean existsByContactEmailAndIdNot(@Param("email") String email, @Param("playerId") UUID playerId);

    /**
     * Find players with recent achievements (last 6 months)
     */
    @Query("SELECT DISTINCT p FROM Player p JOIN p.achievements a " +
           "WHERE p.isActive = true AND a.achievementDate >= :sixMonthsAgo " +
           "ORDER BY p.name ASC")
    List<Player> findPlayersWithRecentAchievements(@Param("sixMonthsAgo") java.time.LocalDate sixMonthsAgo);

    /**
     * Find players without statistics
     */
    @Query("SELECT p FROM Player p WHERE p.isActive = true AND p.statistics IS NULL")
    List<Player> findPlayersWithoutStatistics();

    /**
     * Find players by age range
     */
    @Query("SELECT p FROM Player p WHERE p.isActive = true " +
           "AND p.dateOfBirth IS NOT NULL " +
           "AND YEAR(CURRENT_DATE) - YEAR(p.dateOfBirth) BETWEEN :minAge AND :maxAge " +
           "ORDER BY p.name ASC")
    List<Player> findPlayersByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    /**
     * Get district-wise player statistics
     */
    @Query("SELECT p.district.id, p.district.name, COUNT(p) " +
           "FROM Player p WHERE p.isActive = true AND p.district IS NOT NULL " +
           "GROUP BY p.district.id, p.district.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getDistrictWisePlayerCounts();

    /**
     * Get category-wise player statistics
     */
    @Query("SELECT p.category, COUNT(p) " +
           "FROM Player p WHERE p.isActive = true AND p.category IS NOT NULL " +
           "GROUP BY p.category " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getCategoryWisePlayerCounts();
}