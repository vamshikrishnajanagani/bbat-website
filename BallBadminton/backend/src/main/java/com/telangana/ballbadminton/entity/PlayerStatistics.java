package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing player statistics and performance metrics
 * Contains match records, tournament participation, and rankings
 */
@Entity
@Table(name = "player_statistics", indexes = {
    @Index(name = "idx_player_statistics_player", columnList = "player_id", unique = true),
    @Index(name = "idx_player_statistics_ranking", columnList = "current_ranking"),
    @Index(name = "idx_player_statistics_points", columnList = "total_points")
})
public class PlayerStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_player_statistics_player"))
    private Player player;

    @Min(value = 0, message = "Matches played cannot be negative")
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed = 0;

    @Min(value = 0, message = "Matches won cannot be negative")
    @Column(name = "matches_won", nullable = false)
    private Integer matchesWon = 0;

    @Min(value = 0, message = "Tournaments participated cannot be negative")
    @Column(name = "tournaments_participated", nullable = false)
    private Integer tournamentsParticipated = 0;

    @Min(value = 0, message = "Tournaments won cannot be negative")
    @Column(name = "tournaments_won", nullable = false)
    private Integer tournamentsWon = 0;

    @DecimalMin(value = "0.00", message = "Win percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Win percentage cannot exceed 100")
    @Column(name = "win_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal winPercentage = BigDecimal.ZERO;

    @Min(value = 1, message = "Current ranking must be positive")
    @Column(name = "current_ranking")
    private Integer currentRanking;

    @Min(value = 1, message = "Best ranking must be positive")
    @Column(name = "best_ranking")
    private Integer bestRanking;

    @Min(value = 0, message = "Total points cannot be negative")
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // Constructors
    public PlayerStatistics() {}

    public PlayerStatistics(Player player) {
        this.player = player;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
        updateWinPercentage();
        updateLastUpdated();
    }

    public Integer getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(Integer matchesWon) {
        this.matchesWon = matchesWon;
        updateWinPercentage();
        updateLastUpdated();
    }

    public Integer getTournamentsParticipated() {
        return tournamentsParticipated;
    }

    public void setTournamentsParticipated(Integer tournamentsParticipated) {
        this.tournamentsParticipated = tournamentsParticipated;
        updateLastUpdated();
    }

    public Integer getTournamentsWon() {
        return tournamentsWon;
    }

    public void setTournamentsWon(Integer tournamentsWon) {
        this.tournamentsWon = tournamentsWon;
        updateLastUpdated();
    }

    public BigDecimal getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(BigDecimal winPercentage) {
        this.winPercentage = winPercentage;
    }

    public Integer getCurrentRanking() {
        return currentRanking;
    }

    public void setCurrentRanking(Integer currentRanking) {
        this.currentRanking = currentRanking;
        updateBestRanking();
        updateLastUpdated();
    }

    public Integer getBestRanking() {
        return bestRanking;
    }

    public void setBestRanking(Integer bestRanking) {
        this.bestRanking = bestRanking;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
        updateLastUpdated();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper methods
    private void updateWinPercentage() {
        if (matchesPlayed != null && matchesPlayed > 0 && matchesWon != null) {
            BigDecimal percentage = BigDecimal.valueOf(matchesWon)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(matchesPlayed), 2, java.math.RoundingMode.HALF_UP);
            this.winPercentage = percentage;
        } else {
            this.winPercentage = BigDecimal.ZERO;
        }
    }

    private void updateBestRanking() {
        if (currentRanking != null && (bestRanking == null || currentRanking < bestRanking)) {
            this.bestRanking = currentRanking;
        }
    }

    private void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

    public void addMatchResult(boolean won) {
        this.matchesPlayed = (this.matchesPlayed == null ? 0 : this.matchesPlayed) + 1;
        if (won) {
            this.matchesWon = (this.matchesWon == null ? 0 : this.matchesWon) + 1;
        }
        updateWinPercentage();
        updateLastUpdated();
    }

    public void addTournamentParticipation(boolean won) {
        this.tournamentsParticipated = (this.tournamentsParticipated == null ? 0 : this.tournamentsParticipated) + 1;
        if (won) {
            this.tournamentsWon = (this.tournamentsWon == null ? 0 : this.tournamentsWon) + 1;
        }
        updateLastUpdated();
    }

    public void addPoints(int points) {
        this.totalPoints = (this.totalPoints == null ? 0 : this.totalPoints) + points;
        updateLastUpdated();
    }

    public BigDecimal getTournamentWinPercentage() {
        if (tournamentsParticipated != null && tournamentsParticipated > 0 && tournamentsWon != null) {
            return BigDecimal.valueOf(tournamentsWon)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(tournamentsParticipated), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "PlayerStatistics{" +
                "player=" + (player != null ? player.getName() : null) +
                ", matchesPlayed=" + matchesPlayed +
                ", matchesWon=" + matchesWon +
                ", winPercentage=" + winPercentage +
                ", currentRanking=" + currentRanking +
                ", totalPoints=" + totalPoints +
                '}';
    }
}