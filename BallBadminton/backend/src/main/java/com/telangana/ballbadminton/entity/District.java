package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a district in Telangana state
 * Contains geographic and administrative information
 */
@Entity
@Table(name = "districts", indexes = {
    @Index(name = "idx_districts_code", columnList = "code", unique = true),
    @Index(name = "idx_districts_active", columnList = "is_active")
})
public class District extends BaseEntity {

    @NotBlank(message = "District name is required")
    @Size(max = 100, message = "District name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "District code is required")
    @Size(max = 10, message = "District code must not exceed 10 characters")
    @Column(name = "code", nullable = false, length = 10, unique = true)
    private String code;

    @Size(max = 100, message = "Headquarters name must not exceed 100 characters")
    @Column(name = "headquarters", length = 100)
    private String headquarters;

    @Column(name = "area_sq_km", precision = 10, scale = 2)
    private BigDecimal areaSqKm;

    @Column(name = "population")
    private Long population;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Size(max = 100, message = "Contact email must not exceed 100 characters")
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Size(max = 20, message = "Contact phone must not exceed 20 characters")
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tournament> tournaments = new ArrayList<>();

    // Constructors
    public District() {}

    public District(String name, String code) {
        this.name = name;
        this.code = code;
    }

    // Getters and Setters
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    // Helper methods
    public void addPlayer(Player player) {
        players.add(player);
        player.setDistrict(this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setDistrict(null);
    }

    public void addTournament(Tournament tournament) {
        tournaments.add(tournament);
        tournament.setDistrict(this);
    }

    public void removeTournament(Tournament tournament) {
        tournaments.remove(tournament);
        tournament.setDistrict(null);
    }

    @Override
    public String toString() {
        return "District{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", headquarters='" + headquarters + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}