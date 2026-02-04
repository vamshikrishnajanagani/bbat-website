package com.telangana.ballbadminton.dto.district;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for District operations
 */
public class DistrictResponse {

    private UUID id;
    private String name;
    private String code;
    private String headquarters;
    private BigDecimal areaSqKm;
    private Long population;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private Boolean isActive;
    private Long playerCount;
    private Long tournamentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public DistrictResponse() {}

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public BigDecimal getAreaSqKm() {
        return areaSqKm;
    }

    public void setAreaSqKm(BigDecimal areaSqKm) {
        this.areaSqKm = areaSqKm;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Long playerCount) {
        this.playerCount = playerCount;
    }

    public Long getTournamentCount() {
        return tournamentCount;
    }

    public void setTournamentCount(Long tournamentCount) {
        this.tournamentCount = tournamentCount;
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