package com.telangana.ballbadminton.dto.tournament;

import com.telangana.ballbadminton.entity.Tournament;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating and updating tournaments
 */
public class TournamentRequest {

    @NotBlank(message = "Tournament name is required")
    @Size(max = 200, message = "Tournament name must not exceed 200 characters")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 200, message = "Venue must not exceed 200 characters")
    private String venue;

    private LocalDate registrationStartDate;

    private LocalDate registrationEndDate;

    @Min(value = 1, message = "Max participants must be at least 1")
    private Integer maxParticipants;

    @DecimalMin(value = "0.00", message = "Entry fee cannot be negative")
    private BigDecimal entryFee;

    @DecimalMin(value = "0.00", message = "Prize money cannot be negative")
    private BigDecimal prizeMoney;

    private Tournament.Status status;

    private Tournament.TournamentType tournamentType;

    @Size(max = 50, message = "Age category must not exceed 50 characters")
    private String ageCategory;

    @Size(max = 20, message = "Gender category must not exceed 20 characters")
    private String genderCategory;

    private Boolean isFeatured;

    private UUID districtId;

    // Constructors
    public TournamentRequest() {}

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

    public Tournament.Status getStatus() {
        return status;
    }

    public void setStatus(Tournament.Status status) {
        this.status = status;
    }

    public Tournament.TournamentType getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(Tournament.TournamentType tournamentType) {
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

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }
}
