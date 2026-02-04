package com.telangana.ballbadminton.dto.player;

import com.telangana.ballbadminton.entity.Player;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Player request DTO for creating and updating players
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class PlayerRequest {

    @NotBlank(message = "Player name is required")
    @Size(max = 100, message = "Player name must not exceed 100 characters")
    private String name;

    private LocalDate dateOfBirth;

    private Player.Gender gender;

    private Player.Category category;

    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    private String profilePhotoUrl;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String contactEmail;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String contactPhone;

    private String address;

    private UUID districtId;

    @NotNull
    private Boolean isProminent = false;

    @NotNull
    private Boolean isActive = true;

    // Constructors
    public PlayerRequest() {}

    public PlayerRequest(String name) {
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

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
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

    @Override
    public String toString() {
        return "PlayerRequest{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", gender=" + gender +
                ", isProminent=" + isProminent +
                ", isActive=" + isActive +
                '}';
    }
}