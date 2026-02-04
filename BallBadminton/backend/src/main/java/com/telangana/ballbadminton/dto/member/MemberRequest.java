package com.telangana.ballbadminton.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Member request DTO for creating and updating members
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class MemberRequest {

    @NotBlank(message = "Member name is required")
    @Size(max = 100, message = "Member name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private String biography;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;

    private Integer hierarchyLevel = 0;

    private LocalDate tenureStartDate;

    private LocalDate tenureEndDate;

    @NotNull
    private Boolean isActive = true;

    @NotNull
    private Boolean isProminent = false;

    // Constructors
    public MemberRequest() {}

    public MemberRequest(String name, String position) {
        this.name = name;
        this.position = position;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public LocalDate getTenureStartDate() {
        return tenureStartDate;
    }

    public void setTenureStartDate(LocalDate tenureStartDate) {
        this.tenureStartDate = tenureStartDate;
    }

    public LocalDate getTenureEndDate() {
        return tenureEndDate;
    }

    public void setTenureEndDate(LocalDate tenureEndDate) {
        this.tenureEndDate = tenureEndDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsProminent() {
        return isProminent;
    }

    public void setIsProminent(Boolean isProminent) {
        this.isProminent = isProminent;
    }

    @Override
    public String toString() {
        return "MemberRequest{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", hierarchyLevel=" + hierarchyLevel +
                ", isActive=" + isActive +
                '}';
    }
}