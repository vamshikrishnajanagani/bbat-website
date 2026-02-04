package com.telangana.ballbadminton.dto.player;

import com.telangana.ballbadminton.entity.Achievement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Achievement response DTO for API responses
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class AchievementResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDate achievementDate;
    private String category;
    private Achievement.Level level;
    private Integer position;
    private String positionText;
    private Boolean isVerified;
    private Boolean isRecentAchievement;
    private Boolean isMajorAchievement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Player information
    private UUID playerId;
    private String playerName;

    // Tournament information
    private UUID tournamentId;
    private String tournamentName;

    // Constructors
    public AchievementResponse() {}

    public AchievementResponse(Achievement achievement) {
        this.id = achievement.getId();
        this.title = achievement.getTitle();
        this.description = achievement.getDescription();
        this.achievementDate = achievement.getAchievementDate();
        this.category = achievement.getCategory();
        this.level = achievement.getLevel();
        this.position = achievement.getPosition();
        this.positionText = achievement.getPositionText();
        this.isVerified = achievement.getIsVerified();
        this.isRecentAchievement = achievement.isRecentAchievement();
        this.isMajorAchievement = achievement.isMajorAchievement();
        this.createdAt = achievement.getCreatedAt();
        this.updatedAt = achievement.getUpdatedAt();

        // Player information
        if (achievement.getPlayer() != null) {
            this.playerId = achievement.getPlayer().getId();
            this.playerName = achievement.getPlayer().getName();
        }

        // Tournament information
        if (achievement.getTournament() != null) {
            this.tournamentId = achievement.getTournament().getId();
            this.tournamentName = achievement.getTournament().getName();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAchievementDate() {
        return achievementDate;
    }

    public void setAchievementDate(LocalDate achievementDate) {
        this.achievementDate = achievementDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Achievement.Level getLevel() {
        return level;
    }

    public void setLevel(Achievement.Level level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getPositionText() {
        return positionText;
    }

    public void setPositionText(String positionText) {
        this.positionText = positionText;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsRecentAchievement() {
        return isRecentAchievement;
    }

    public void setIsRecentAchievement(Boolean isRecentAchievement) {
        this.isRecentAchievement = isRecentAchievement;
    }

    public Boolean getIsMajorAchievement() {
        return isMajorAchievement;
    }

    public void setIsMajorAchievement(Boolean isMajorAchievement) {
        this.isMajorAchievement = isMajorAchievement;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

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
}