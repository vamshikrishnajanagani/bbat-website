package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing player achievements and accomplishments
 * Contains details about awards, tournament wins, and recognitions
 */
@Entity
@Table(name = "achievements", indexes = {
    @Index(name = "idx_achievements_player", columnList = "player_id"),
    @Index(name = "idx_achievements_tournament", columnList = "tournament_id"),
    @Index(name = "idx_achievements_level", columnList = "level"),
    @Index(name = "idx_achievements_date", columnList = "achievement_date"),
    @Index(name = "idx_achievements_verified", columnList = "is_verified")
})
public class Achievement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_achievement_player"))
    private Player player;

    @NotBlank(message = "Achievement title is required")
    @Size(max = 200, message = "Achievement title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "achievement_date")
    private LocalDate achievementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", 
                foreignKey = @ForeignKey(name = "fk_achievement_tournament"))
    private Tournament tournament;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(name = "category", length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 50)
    private Level level;

    @Min(value = 1, message = "Position must be positive")
    @Column(name = "position")
    private Integer position;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    // Enums
    public enum Level {
        DISTRICT("District"),
        STATE("State"),
        NATIONAL("National"),
        INTERNATIONAL("International");

        private final String displayName;

        Level(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Achievement() {}

    public Achievement(String title, Player player) {
        this.title = title;
        this.player = player;
    }

    // Getters and Setters
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    // Helper methods
    public String getPositionText() {
        if (position == null) {
            return null;
        }
        
        switch (position) {
            case 1: return "1st Place";
            case 2: return "2nd Place";
            case 3: return "3rd Place";
            default: return position + "th Place";
        }
    }

    public boolean isRecentAchievement() {
        if (achievementDate == null) {
            return false;
        }
        return achievementDate.isAfter(LocalDate.now().minusMonths(6));
    }

    public boolean isMajorAchievement() {
        return level == Level.NATIONAL || level == Level.INTERNATIONAL || 
               (position != null && position <= 3);
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "title='" + title + '\'' +
                ", level=" + level +
                ", position=" + position +
                ", achievementDate=" + achievementDate +
                ", isVerified=" + isVerified +
                '}';
    }
}