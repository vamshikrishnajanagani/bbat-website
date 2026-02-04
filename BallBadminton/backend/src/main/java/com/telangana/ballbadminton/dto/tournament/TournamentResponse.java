package com.telangana.ballbadminton.dto.tournament;

import com.telangana.ballbadminton.entity.Tournament;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for tournament responses
 */
public class TournamentResponse {

    private UUID id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private Integer maxParticipants;
    private BigDecimal entryFee;
    private BigDecimal prizeMoney;
    private Tournament.Status status;
    private Tournament.TournamentType tournamentType;
    private String ageCategory;
    private String genderCategory;
    private Boolean isFeatured;
    private UUID districtId;
    private String districtName;
    private Integer currentRegistrationCount;
    private Boolean hasAvailableSlots;
    private Boolean isRegistrationOpen;
    private Integer durationInDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TournamentResponse() {}

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

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getCurrentRegistrationCount() {
        return currentRegistrationCount;
    }

    public void setCurrentRegistrationCount(Integer currentRegistrationCount) {
        this.currentRegistrationCount = currentRegistrationCount;
    }

    public Boolean getHasAvailableSlots() {
        return hasAvailableSlots;
    }

    public void setHasAvailableSlots(Boolean hasAvailableSlots) {
        this.hasAvailableSlots = hasAvailableSlots;
    }

    public Boolean getIsRegistrationOpen() {
        return isRegistrationOpen;
    }

    public void setIsRegistrationOpen(Boolean isRegistrationOpen) {
        this.isRegistrationOpen = isRegistrationOpen;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
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
}
