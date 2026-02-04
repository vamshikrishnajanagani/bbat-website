package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Achievement entity
 * Provides data access methods for achievement management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    /**
     * Find achievements by player ID
     */
    List<Achievement> findByPlayerIdOrderByAchievementDateDesc(UUID playerId);

    /**
     * Find achievements by player ID with pagination
     */
    Page<Achievement> findByPlayerIdOrderByAchievementDateDesc(UUID playerId, Pageable pageable);

    /**
     * Find verified achievements by player ID
     */
    List<Achievement> findByPlayerIdAndIsVerifiedTrueOrderByAchievementDateDesc(UUID playerId);

    /**
     * Find recent achievements by player ID (last 6 months)
     */
    @Query("SELECT a FROM Achievement a WHERE a.player.id = :playerId " +
           "AND a.achievementDate >= :sixMonthsAgo " +
           "ORDER BY a.achievementDate DESC")
    List<Achievement> findRecentAchievementsByPlayerId(@Param("playerId") UUID playerId, 
                                                      @Param("sixMonthsAgo") LocalDate sixMonthsAgo);

    /**
     * Find major achievements by player ID (National/International or top 3 positions)
     */
    @Query("SELECT a FROM Achievement a WHERE a.player.id = :playerId " +
           "AND (a.level IN ('NATIONAL', 'INTERNATIONAL') OR a.position <= 3) " +
           "ORDER BY a.achievementDate DESC")
    List<Achievement> findMajorAchievementsByPlayerId(@Param("playerId") UUID playerId);

    /**
     * Find achievements by level
     */
    List<Achievement> findByLevelOrderByAchievementDateDesc(Achievement.Level level);

    /**
     * Find achievements by category
     */
    List<Achievement> findByCategoryOrderByAchievementDateDesc(String category);

    /**
     * Find achievements by tournament
     */
    List<Achievement> findByTournament_IdOrderByPositionAsc(UUID tournamentId);

    /**
     * Find top achievements (position 1-3) with pagination
     */
    @Query("SELECT a FROM Achievement a WHERE a.position BETWEEN 1 AND 3 " +
           "ORDER BY a.achievementDate DESC, a.position ASC")
    Page<Achievement> findTopAchievements(Pageable pageable);

    /**
     * Find recent achievements across all players
     */
    @Query("SELECT a FROM Achievement a WHERE a.achievementDate >= :date " +
           "ORDER BY a.achievementDate DESC")
    List<Achievement> findRecentAchievements(@Param("date") LocalDate date, Pageable pageable);

    /**
     * Find unverified achievements
     */
    List<Achievement> findByIsVerifiedFalseOrderByCreatedAtDesc();

    /**
     * Count achievements by player
     */
    long countByPlayerId(UUID playerId);

    /**
     * Count verified achievements by player
     */
    long countByPlayerIdAndIsVerifiedTrue(UUID playerId);

    /**
     * Count major achievements by player
     */
    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.player.id = :playerId " +
           "AND (a.level IN ('NATIONAL', 'INTERNATIONAL') OR a.position <= 3)")
    long countMajorAchievementsByPlayerId(@Param("playerId") UUID playerId);

    /**
     * Get achievement statistics by level
     */
    @Query("SELECT a.level, COUNT(a) FROM Achievement a " +
           "WHERE a.level IS NOT NULL " +
           "GROUP BY a.level " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getAchievementCountsByLevel();

    /**
     * Get achievement statistics by category
     */
    @Query("SELECT a.category, COUNT(a) FROM Achievement a " +
           "WHERE a.category IS NOT NULL " +
           "GROUP BY a.category " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getAchievementCountsByCategory();

    /**
     * Get monthly achievement statistics for the current year
     */
    @Query("SELECT MONTH(a.achievementDate), COUNT(a) FROM Achievement a " +
           "WHERE YEAR(a.achievementDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(a.achievementDate) " +
           "ORDER BY MONTH(a.achievementDate)")
    List<Object[]> getMonthlyAchievementCounts();

    /**
     * Find achievements with search term
     */
    @Query("SELECT a FROM Achievement a WHERE " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY a.achievementDate DESC")
    Page<Achievement> searchAchievements(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find achievements with advanced filtering
     */
    @Query("SELECT a FROM Achievement a WHERE " +
           "(:level IS NULL OR a.level = :level) " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:isVerified IS NULL OR a.isVerified = :isVerified) " +
           "AND (:playerId IS NULL OR a.player.id = :playerId) " +
           "AND (:tournamentId IS NULL OR a.tournament.id = :tournamentId) " +
           "AND (:startDate IS NULL OR a.achievementDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.achievementDate <= :endDate) " +
           "ORDER BY a.achievementDate DESC")
    Page<Achievement> findAchievementsWithFilters(
            @Param("level") Achievement.Level level,
            @Param("category") String category,
            @Param("isVerified") Boolean isVerified,
            @Param("playerId") UUID playerId,
            @Param("tournamentId") UUID tournamentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}