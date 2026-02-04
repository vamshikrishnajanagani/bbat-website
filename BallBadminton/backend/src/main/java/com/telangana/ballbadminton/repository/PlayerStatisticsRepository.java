package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.PlayerStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PlayerStatistics entity
 * Provides data access methods for player statistics management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, UUID> {

    /**
     * Find statistics by player ID
     */
    Optional<PlayerStatistics> findByPlayerId(UUID playerId);

    /**
     * Find players with rankings (current ranking not null)
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.currentRanking IS NOT NULL " +
           "ORDER BY ps.currentRanking ASC")
    List<PlayerStatistics> findPlayersWithRankings();

    /**
     * Find top ranked players
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.currentRanking IS NOT NULL " +
           "ORDER BY ps.currentRanking ASC")
    List<PlayerStatistics> findTopRankedPlayers(Pageable pageable);

    /**
     * Find players by ranking range
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.currentRanking BETWEEN :startRank AND :endRank " +
           "ORDER BY ps.currentRanking ASC")
    List<PlayerStatistics> findPlayersByRankingRange(@Param("startRank") int startRank, 
                                                     @Param("endRank") int endRank);

    /**
     * Find players with highest win percentage (minimum matches required)
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.matchesPlayed >= :minMatches " +
           "ORDER BY ps.winPercentage DESC, ps.matchesPlayed DESC")
    List<PlayerStatistics> findPlayersWithHighestWinPercentage(@Param("minMatches") int minMatches, 
                                                               Pageable pageable);

    /**
     * Find players with most tournament wins
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.tournamentsWon > 0 " +
           "ORDER BY ps.tournamentsWon DESC, ps.tournamentsParticipated ASC")
    List<PlayerStatistics> findPlayersWithMostTournamentWins(Pageable pageable);

    /**
     * Find players with most total points
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.totalPoints > 0 " +
           "ORDER BY ps.totalPoints DESC")
    List<PlayerStatistics> findPlayersWithMostPoints(Pageable pageable);

    /**
     * Find players with most matches played
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.matchesPlayed > 0 " +
           "ORDER BY ps.matchesPlayed DESC")
    List<PlayerStatistics> findPlayersWithMostMatches(Pageable pageable);

    /**
     * Get average statistics across all players
     */
    @Query("SELECT AVG(ps.matchesPlayed), AVG(ps.matchesWon), AVG(ps.winPercentage), " +
           "AVG(ps.tournamentsParticipated), AVG(ps.tournamentsWon), AVG(ps.totalPoints) " +
           "FROM PlayerStatistics ps WHERE ps.matchesPlayed > 0")
    Object[] getAverageStatistics();

    /**
     * Count players with statistics
     */
    @Query("SELECT COUNT(ps) FROM PlayerStatistics ps WHERE ps.matchesPlayed > 0")
    long countPlayersWithStatistics();

    /**
     * Count players with rankings
     */
    @Query("SELECT COUNT(ps) FROM PlayerStatistics ps WHERE ps.currentRanking IS NOT NULL")
    long countPlayersWithRankings();

    /**
     * Get win percentage distribution
     */
    @Query("SELECT " +
           "SUM(CASE WHEN ps.winPercentage >= 80 THEN 1 ELSE 0 END) as excellent, " +
           "SUM(CASE WHEN ps.winPercentage >= 60 AND ps.winPercentage < 80 THEN 1 ELSE 0 END) as good, " +
           "SUM(CASE WHEN ps.winPercentage >= 40 AND ps.winPercentage < 60 THEN 1 ELSE 0 END) as average, " +
           "SUM(CASE WHEN ps.winPercentage < 40 THEN 1 ELSE 0 END) as poor " +
           "FROM PlayerStatistics ps WHERE ps.matchesPlayed >= 10")
    Object[] getWinPercentageDistribution();

    /**
     * Get tournament participation statistics
     */
    @Query("SELECT " +
           "SUM(CASE WHEN ps.tournamentsParticipated >= 10 THEN 1 ELSE 0 END) as highParticipation, " +
           "SUM(CASE WHEN ps.tournamentsParticipated >= 5 AND ps.tournamentsParticipated < 10 THEN 1 ELSE 0 END) as mediumParticipation, " +
           "SUM(CASE WHEN ps.tournamentsParticipated > 0 AND ps.tournamentsParticipated < 5 THEN 1 ELSE 0 END) as lowParticipation, " +
           "SUM(CASE WHEN ps.tournamentsParticipated = 0 THEN 1 ELSE 0 END) as noParticipation " +
           "FROM PlayerStatistics ps")
    Object[] getTournamentParticipationDistribution();

    /**
     * Find statistics that need ranking update (no current ranking but has points)
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.currentRanking IS NULL " +
           "AND ps.totalPoints > 0 ORDER BY ps.totalPoints DESC")
    List<PlayerStatistics> findStatisticsNeedingRankingUpdate();

    /**
     * Find next available ranking position
     */
    @Query("SELECT COALESCE(MAX(ps.currentRanking), 0) + 1 FROM PlayerStatistics ps " +
           "WHERE ps.currentRanking IS NOT NULL")
    Integer findNextAvailableRanking();

    /**
     * Check if ranking position is available
     */
    @Query("SELECT COUNT(ps) = 0 FROM PlayerStatistics ps WHERE ps.currentRanking = :ranking")
    boolean isRankingPositionAvailable(@Param("ranking") int ranking);

    /**
     * Find statistics by ranking position
     */
    Optional<PlayerStatistics> findByCurrentRanking(int ranking);

    /**
     * Update rankings for players with points (bulk ranking calculation)
     */
    @Query("SELECT ps FROM PlayerStatistics ps WHERE ps.totalPoints > 0 " +
           "ORDER BY ps.totalPoints DESC, ps.winPercentage DESC, ps.tournamentsWon DESC")
    List<PlayerStatistics> findPlayersForRankingCalculation();
}