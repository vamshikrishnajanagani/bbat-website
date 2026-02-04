package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlayerStatistics entity
 */
class PlayerStatisticsTest {

    private PlayerStatistics statistics;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        statistics = new PlayerStatistics(player);
    }

    @Test
    void testStatisticsCreation() {
        assertNotNull(statistics);
        assertEquals(player, statistics.getPlayer());
        assertEquals(0, statistics.getMatchesPlayed());
        assertEquals(0, statistics.getMatchesWon());
        assertEquals(0, statistics.getTournamentsParticipated());
        assertEquals(0, statistics.getTournamentsWon());
        assertEquals(BigDecimal.ZERO, statistics.getWinPercentage());
        assertEquals(0, statistics.getTotalPoints());
    }

    @Test
    void testWinPercentageCalculation() {
        statistics.setMatchesPlayed(10);
        statistics.setMatchesWon(7);

        assertEquals(new BigDecimal("70.00"), statistics.getWinPercentage());
    }

    @Test
    void testWinPercentageWithZeroMatches() {
        statistics.setMatchesPlayed(0);
        statistics.setMatchesWon(0);

        assertEquals(BigDecimal.ZERO, statistics.getWinPercentage());
    }

    @Test
    void testWinPercentageWithNullValues() {
        statistics.setMatchesPlayed(null);
        statistics.setMatchesWon(null);

        assertEquals(BigDecimal.ZERO, statistics.getWinPercentage());
    }

    @Test
    void testBestRankingUpdate() {
        // Set initial ranking
        statistics.setCurrentRanking(5);
        assertEquals(5, statistics.getBestRanking());

        // Improve ranking
        statistics.setCurrentRanking(3);
        assertEquals(3, statistics.getBestRanking());

        // Worse ranking should not change best ranking
        statistics.setCurrentRanking(7);
        assertEquals(3, statistics.getBestRanking());
    }

    @Test
    void testAddMatchResult() {
        // Add a won match
        statistics.addMatchResult(true);
        assertEquals(1, statistics.getMatchesPlayed());
        assertEquals(1, statistics.getMatchesWon());
        assertEquals(new BigDecimal("100.00"), statistics.getWinPercentage());

        // Add a lost match
        statistics.addMatchResult(false);
        assertEquals(2, statistics.getMatchesPlayed());
        assertEquals(1, statistics.getMatchesWon());
        assertEquals(new BigDecimal("50.00"), statistics.getWinPercentage());
    }

    @Test
    void testAddTournamentParticipation() {
        // Add a won tournament
        statistics.addTournamentParticipation(true);
        assertEquals(1, statistics.getTournamentsParticipated());
        assertEquals(1, statistics.getTournamentsWon());

        // Add a lost tournament
        statistics.addTournamentParticipation(false);
        assertEquals(2, statistics.getTournamentsParticipated());
        assertEquals(1, statistics.getTournamentsWon());
    }

    @Test
    void testAddPoints() {
        statistics.addPoints(100);
        assertEquals(100, statistics.getTotalPoints());

        statistics.addPoints(50);
        assertEquals(150, statistics.getTotalPoints());
    }

    @Test
    void testTournamentWinPercentage() {
        statistics.setTournamentsParticipated(5);
        statistics.setTournamentsWon(2);

        assertEquals(new BigDecimal("40.00"), statistics.getTournamentWinPercentage());
    }

    @Test
    void testTournamentWinPercentageWithZeroTournaments() {
        statistics.setTournamentsParticipated(0);
        statistics.setTournamentsWon(0);

        assertEquals(BigDecimal.ZERO, statistics.getTournamentWinPercentage());
    }

    @Test
    void testLastUpdatedIsSet() {
        assertNotNull(statistics.getLastUpdated());
    }

    @Test
    void testLastUpdatedIsUpdatedOnChanges() {
        var initialTime = statistics.getLastUpdated();
        
        // Small delay to ensure time difference
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        statistics.setMatchesPlayed(5);
        assertTrue(statistics.getLastUpdated().isAfter(initialTime));
    }

    @Test
    void testToString() {
        String toString = statistics.toString();
        assertTrue(toString.contains("Test Player"));
        assertTrue(toString.contains("matchesPlayed=0"));
        assertTrue(toString.contains("matchesWon=0"));
        assertTrue(toString.contains("winPercentage=0"));
    }

    @Test
    void testComplexScenario() {
        // Simulate a player's career
        statistics.addMatchResult(true);  // Win
        statistics.addMatchResult(true);  // Win
        statistics.addMatchResult(false); // Loss
        statistics.addMatchResult(true);  // Win

        statistics.addTournamentParticipation(true);  // Tournament win
        statistics.addTournamentParticipation(false); // Tournament loss

        statistics.setCurrentRanking(10);
        statistics.setCurrentRanking(5);
        statistics.setCurrentRanking(8);

        statistics.addPoints(250);

        // Verify final state
        assertEquals(4, statistics.getMatchesPlayed());
        assertEquals(3, statistics.getMatchesWon());
        assertEquals(new BigDecimal("75.00"), statistics.getWinPercentage());
        assertEquals(2, statistics.getTournamentsParticipated());
        assertEquals(1, statistics.getTournamentsWon());
        assertEquals(new BigDecimal("50.00"), statistics.getTournamentWinPercentage());
        assertEquals(8, statistics.getCurrentRanking());
        assertEquals(5, statistics.getBestRanking());
        assertEquals(250, statistics.getTotalPoints());
    }
}