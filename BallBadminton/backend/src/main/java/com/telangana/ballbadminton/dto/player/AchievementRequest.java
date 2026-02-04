package com.telangana.ballbadminton.dto.player;

import com.telangana.ballbadminton.entity.Achievement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Achievement request DTO for creating and updating achievements
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class AchievementRequest {

    @NotBlank(message = "Achievement title is required")
    @Size(max = 200, message = "Achievement title must not exceed 200 characters")
    private String title;

    private String description;

    private LocalDate achievementDate;

    private UUID tournamentId;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    private Achievement.Level level;

    @Min(value = 1, message = "Position must be positive")
    private Integer position;

    private Boolean isVerified = false;

    // Constructors
    public AchievementRequest() {}

    public AchievementRequest(String title) {
        this.title = title;
    }

    // Getters and Setters
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

    public UUID getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(UUID tournamentId) {
        this.tournamentId = tournamentId;
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

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    @Override
    public String toString() {
        return "AchievementRequest{" +
                "title='" + title + '\'' +
                ", level=" + level +
                ", position=" + position +
                ", achievementDate=" + achievementDate +
                '}';
    }
}