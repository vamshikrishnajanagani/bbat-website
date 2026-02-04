package com.telangana.ballbadminton.dto.player;

import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.entity.Achievement;
import com.telangana.ballbadminton.entity.PlayerStatistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player response DTO for API responses
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class PlayerResponse {

    private UUID id;
    private String name;
    private LocalDate dateOfBirth;
    private Integer age;
    private Player.Gender gender;
    private Player.Category category;
    private String profilePhotoUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Boolean isProminent;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // District information
    private UUID districtId;
    private String districtName;

    // Statistics
    private PlayerStatisticsResponse statistics;

    // Recent achievements (last 5)
    private List<AchievementResponse> recentAchievements;

    // Total achievements count
    private Integer totalAchievements;

    // Constructors
    public PlayerResponse() {}

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.dateOfBirth = player.getDateOfBirth();
        this.age = player.getAge();
        this.gender = player.getGender();
        this.category = player.getCategory();
        this.profilePhotoUrl = player.getProfilePhotoUrl();
        this.contactEmail = player.getContactEmail();
        this.contactPhone = player.getContactPhone();
        this.address = player.getAddress();
        this.isProminent = player.getIsProminent();
        this.isActive = player.getIsActive();
        this.createdAt = player.getCreatedAt();
        this.updatedAt = player.getUpdatedAt();

        // District information
        if (player.getDistrict() != null) {
            this.districtId = player.getDistrict().getId();
            this.districtName = player.getDistrict().getName();
        }

        // Statistics
        if (player.getStatistics() != null) {
            this.statistics = new PlayerStatisticsResponse(player.getStatistics());
        }

        // Recent achievements (last 5)
        if (player.getAchievements() != null) {
            this.totalAchievements = player.getAchievements().size();
            this.recentAchievements = player.getAchievements().stream()
                    .sorted((a1, a2) -> {
                        if (a1.getAchievementDate() == null && a2.getAchievementDate() == null) return 0;
                        if (a1.getAchievementDate() == null) return 1;
                        if (a2.getAchievementDate() == null) return -1;
                        return a2.getAchievementDate().compareTo(a1.getAchievementDate());
                    })
                    .limit(5)
                    .map(AchievementResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.totalAchievements = 0;
            this.recentAchievements = List.of();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Player.Gender getGender() {
        return gender;
    }

    public void setGender(Player.Gender gender) {
        this.gender = gender;
    }

    public Player.Category getCategory() {
        return category;
    }

    public void setCategory(Player.Category category) {
        this.category = category;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsProminent() {
        return isProminent;
    }

    public void setIsProminent(Boolean isProminent) {
        this.isProminent = isProminent;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public PlayerStatisticsResponse getStatistics() {
        return statistics;
    }

    public void setStatistics(PlayerStatisticsResponse statistics) {
        this.statistics = statistics;
    }

    public List<AchievementResponse> getRecentAchievements() {
        return recentAchievements;
    }

    public void setRecentAchievements(List<AchievementResponse> recentAchievements) {
        this.recentAchievements = recentAchievements;
    }

    public Integer getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(Integer totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    /**
     * Nested class for player statistics response
     */
    public static class PlayerStatisticsResponse {
        private Integer matchesPlayed;
        private Integer matchesWon;
        private Integer tournamentsParticipated;
        private Integer tournamentsWon;
        private Double winPercentage;
        private Integer currentRanking;
        private Integer bestRanking;
        private Integer totalPoints;

        public PlayerStatisticsResponse() {}

        public PlayerStatisticsResponse(PlayerStatistics stats) {
            this.matchesPlayed = stats.getMatchesPlayed();
            this.matchesWon = stats.getMatchesWon();
            this.tournamentsParticipated = stats.getTournamentsParticipated();
            this.tournamentsWon = stats.getTournamentsWon();
            this.winPercentage = stats.getWinPercentage() != null ? stats.getWinPercentage().doubleValue() : 0.0;
            this.currentRanking = stats.getCurrentRanking();
            this.bestRanking = stats.getBestRanking();
            this.totalPoints = stats.getTotalPoints();
        }

        // Getters and Setters
        public Integer getMatchesPlayed() { return matchesPlayed; }
        public void setMatchesPlayed(Integer matchesPlayed) { this.matchesPlayed = matchesPlayed; }

        public Integer getMatchesWon() { return matchesWon; }
        public void setMatchesWon(Integer matchesWon) { this.matchesWon = matchesWon; }

        public Integer getTournamentsParticipated() { return tournamentsParticipated; }
        public void setTournamentsParticipated(Integer tournamentsParticipated) { this.tournamentsParticipated = tournamentsParticipated; }

        public Integer getTournamentsWon() { return tournamentsWon; }
        public void setTournamentsWon(Integer tournamentsWon) { this.tournamentsWon = tournamentsWon; }

        public Double getWinPercentage() { return winPercentage; }
        public void setWinPercentage(Double winPercentage) { this.winPercentage = winPercentage; }

        public Integer getCurrentRanking() { return currentRanking; }
        public void setCurrentRanking(Integer currentRanking) { this.currentRanking = currentRanking; }

        public Integer getBestRanking() { return bestRanking; }
        public void setBestRanking(Integer bestRanking) { this.bestRanking = bestRanking; }

        public Integer getTotalPoints() { return totalPoints; }
        public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    }

    /**
     * Nested class for achievement response
     */
    public static class AchievementResponse {
        private UUID id;
        private String title;
        private String description;
        private LocalDate achievementDate;
        private String category;
        private Achievement.Level level;
        private Integer position;
        private Boolean isVerified;

        public AchievementResponse() {}

        public AchievementResponse(Achievement achievement) {
            this.id = achievement.getId();
            this.title = achievement.getTitle();
            this.description = achievement.getDescription();
            this.achievementDate = achievement.getAchievementDate();
            this.category = achievement.getCategory();
            this.level = achievement.getLevel();
            this.position = achievement.getPosition();
            this.isVerified = achievement.getIsVerified();
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDate getAchievementDate() { return achievementDate; }
        public void setAchievementDate(LocalDate achievementDate) { this.achievementDate = achievementDate; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public Achievement.Level getLevel() { return level; }
        public void setLevel(Achievement.Level level) { this.level = level; }

        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }

        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    }
}