package com.telangana.ballbadminton.entity;

import com.telangana.ballbadminton.validation.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Ball Badminton tournament
 * Contains tournament information, categories, and registration details
 */
@Entity
@ValidDateRange(startDateField = "startDate", endDateField = "endDate", 
                message = "Tournament start date must be before or equal to end date")
@ValidDateRange(startDateField = "registrationStartDate", endDateField = "registrationEndDate", 
                message = "Registration start date must be before or equal to registration end date")
@Table(name = "tournaments", indexes = {
    @Index(name = "idx_tournaments_status", columnList = "status"),
    @Index(name = "idx_tournaments_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_tournaments_district", columnList = "district_id"),
    @Index(name = "idx_tournaments_featured", columnList = "is_featured")
})
public class Tournament extends BaseEntity {

    @NotBlank(message = "Tournament name is required")
    @Size(max = 200, message = "Tournament name must not exceed 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Size(max = 200, message = "Venue must not exceed 200 characters")
    @Column(name = "venue", length = 200)
    private String venue;

    @Column(name = "registration_start_date")
    private LocalDate registrationStartDate;

    @Column(name = "registration_end_date")
    private LocalDate registrationEndDate;

    @Min(value = 1, message = "Max participants must be at least 1")
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @DecimalMin(value = "0.00", message = "Entry fee cannot be negative")
    @Column(name = "entry_fee", precision = 10, scale = 2)
    private BigDecimal entryFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Prize money cannot be negative")
    @Column(name = "prize_money", precision = 12, scale = 2)
    private BigDecimal prizeMoney = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.UPCOMING;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", length = 50)
    private TournamentType tournamentType;

    @Size(max = 50, message = "Age category must not exceed 50 characters")
    @Column(name = "age_category", length = 50)
    private String ageCategory;

    @Size(max = 20, message = "Gender category must not exceed 20 characters")
    @Column(name = "gender_category", length = 20)
    private String genderCategory;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id",
                foreignKey = @ForeignKey(name = "fk_tournament_district"))
    private District district;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentRegistration> registrations = new ArrayList<>();

    // Enums
    public enum Status {
        UPCOMING("Upcoming"),
        REGISTRATION_OPEN("Registration Open"),
        REGISTRATION_CLOSED("Registration Closed"),
        ONGOING("Ongoing"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TournamentType {
        SINGLES("Singles"),
        DOUBLES("Doubles"),
        MIXED("Mixed"),
        TEAM("Team");

        private final String displayName;

        TournamentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Tournament() {}

    public Tournament(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDate registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDate getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(LocalDate registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public BigDecimal getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }

    public BigDecimal getPrizeMoney() {
        return prizeMoney;
    }

    public void setPrizeMoney(BigDecimal prizeMoney) {
        this.prizeMoney = prizeMoney;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TournamentType getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(TournamentType tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(String ageCategory) {
        this.ageCategory = ageCategory;
    }

    public String getGenderCategory() {
        return genderCategory;
    }

    public void setGenderCategory(String genderCategory) {
        this.genderCategory = genderCategory;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public List<TournamentRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<TournamentRegistration> registrations) {
        this.registrations = registrations;
    }

    // Helper methods
    public void addRegistration(TournamentRegistration registration) {
        registrations.add(registration);
        registration.setTournament(this);
    }

    public void removeRegistration(TournamentRegistration registration) {
        registrations.remove(registration);
        registration.setTournament(null);
    }

    public boolean isRegistrationOpen() {
        LocalDate now = LocalDate.now();
        return status == Status.REGISTRATION_OPEN &&
               (registrationStartDate == null || !registrationStartDate.isAfter(now)) &&
               (registrationEndDate == null || !registrationEndDate.isBefore(now));
    }

    public boolean isUpcoming() {
        return status == Status.UPCOMING || status == Status.REGISTRATION_OPEN || status == Status.REGISTRATION_CLOSED;
    }

    public boolean isOngoing() {
        return status == Status.ONGOING;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public int getCurrentRegistrationCount() {
        return (int) registrations.stream()
                .filter(reg -> reg.getStatus() == TournamentRegistration.RegistrationStatus.REGISTERED ||
                              reg.getStatus() == TournamentRegistration.RegistrationStatus.CONFIRMED)
                .count();
    }

    public boolean hasAvailableSlots() {
        return maxParticipants == null || getCurrentRegistrationCount() < maxParticipants;
    }

    public int getDurationInDays() {
        if (startDate != null && endDate != null) {
            return (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", venue='" + venue + '\'' +
                ", status=" + status +
                ", district=" + (district != null ? district.getName() : null) +
                '}';
    }
}