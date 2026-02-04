package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Ball Badminton player
 * Contains player profile information, statistics, and achievements
 */
@Entity
@Table(name = "players", indexes = {
    @Index(name = "idx_players_district", columnList = "district_id"),
    @Index(name = "idx_players_category", columnList = "category"),
    @Index(name = "idx_players_prominent", columnList = "is_prominent"),
    @Index(name = "idx_players_active", columnList = "is_active")
})
public class Player extends BaseEntity {

    @NotBlank(message = "Player name is required")
    @Size(max = 100, message = "Player name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private Category category;

    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @NotNull
    @Column(name = "is_prominent", nullable = false)
    private Boolean isProminent = false;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id",
                foreignKey = @ForeignKey(name = "fk_player_district"))
    private District district;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Achievement> achievements = new ArrayList<>();

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PlayerStatistics statistics;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TournamentRegistration> tournamentRegistrations = new ArrayList<>();

    // Enums
    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        OTHER("Other");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Category {
        MEN("Men"),
        WOMEN("Women"),
        JUNIOR("Junior"),
        SENIOR("Senior"),
        VETERANS("Veterans");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Player() {}

    public Player(String name) {
        this.name = name;
    }

    // Getters and Setters
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(PlayerStatistics statistics) {
        this.statistics = statistics;
        if (statistics != null) {
            statistics.setPlayer(this);
        }
    }

    public List<TournamentRegistration> getTournamentRegistrations() {
        return tournamentRegistrations;
    }

    public void setTournamentRegistrations(List<TournamentRegistration> tournamentRegistrations) {
        this.tournamentRegistrations = tournamentRegistrations;
    }

    // Helper methods
    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
        achievement.setPlayer(this);
    }

    public void removeAchievement(Achievement achievement) {
        achievements.remove(achievement);
        achievement.setPlayer(null);
    }

    public void addTournamentRegistration(TournamentRegistration registration) {
        tournamentRegistrations.add(registration);
        registration.setPlayer(this);
    }

    public void removeTournamentRegistration(TournamentRegistration registration) {
        tournamentRegistrations.remove(registration);
        registration.setPlayer(null);
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", district=" + (district != null ? district.getName() : null) +
                ", isProminent=" + isProminent +
                ", isActive=" + isActive +
                '}';
    }
}