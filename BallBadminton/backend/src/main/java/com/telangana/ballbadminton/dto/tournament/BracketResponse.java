package com.telangana.ballbadminton.dto.tournament;

import java.util.List;
import java.util.UUID;

/**
 * DTO for tournament bracket responses
 */
public class BracketResponse {

    private UUID tournamentId;
    private String tournamentName;
    private List<Round> rounds;
    private Integer totalRounds;

    // Constructors
    public BracketResponse() {}

    // Getters and Setters
    public UUID getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(UUID tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    // Inner classes
    public static class Round {
        private Integer roundNumber;
        private String roundName;
        private List<Match> matches;

        public Round() {}

        public Integer getRoundNumber() {
            return roundNumber;
        }

        public void setRoundNumber(Integer roundNumber) {
            this.roundNumber = roundNumber;
        }

        public String getRoundName() {
            return roundName;
        }

        public void setRoundName(String roundName) {
            this.roundName = roundName;
        }

        public List<Match> getMatches() {
            return matches;
        }

        public void setMatches(List<Match> matches) {
            this.matches = matches;
        }
    }

    public static class Match {
        private Integer matchNumber;
        private UUID player1Id;
        private String player1Name;
        private UUID player2Id;
        private String player2Name;
        private UUID winnerId;
        private String winnerName;
        private String score;
        private MatchStatus status;

        public Match() {}

        public Integer getMatchNumber() {
            return matchNumber;
        }

        public void setMatchNumber(Integer matchNumber) {
            this.matchNumber = matchNumber;
        }

        public UUID getPlayer1Id() {
            return player1Id;
        }

        public void setPlayer1Id(UUID player1Id) {
            this.player1Id = player1Id;
        }

        public String getPlayer1Name() {
            return player1Name;
        }

        public void setPlayer1Name(String player1Name) {
            this.player1Name = player1Name;
        }

        public UUID getPlayer2Id() {
            return player2Id;
        }

        public void setPlayer2Id(UUID player2Id) {
            this.player2Id = player2Id;
        }

        public String getPlayer2Name() {
            return player2Name;
        }

        public void setPlayer2Name(String player2Name) {
            this.player2Name = player2Name;
        }

        public UUID getWinnerId() {
            return winnerId;
        }

        public void setWinnerId(UUID winnerId) {
            this.winnerId = winnerId;
        }

        public String getWinnerName() {
            return winnerName;
        }

        public void setWinnerName(String winnerName) {
            this.winnerName = winnerName;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public MatchStatus getStatus() {
            return status;
        }

        public void setStatus(MatchStatus status) {
            this.status = status;
        }
    }

    public enum MatchStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        WALKOVER
    }
}
