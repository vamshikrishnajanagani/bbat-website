package com.telangana.ballbadminton.dto.member;

import com.telangana.ballbadminton.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Member response DTO for API responses
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class MemberResponse {

    private UUID id;
    private String name;
    private String position;
    private String email;
    private String phone;
    private String biography;
    private String photoUrl;
    private Integer hierarchyLevel;
    private LocalDate tenureStartDate;
    private LocalDate tenureEndDate;
    private Boolean isActive;
    private Boolean isProminent;
    private Boolean isCurrentlyServing;
    private Boolean hasTenureExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public MemberResponse() {}

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.position = member.getPosition();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.biography = member.getBiography();
        this.photoUrl = member.getPhotoUrl();
        this.hierarchyLevel = member.getHierarchyLevel();
        this.tenureStartDate = member.getTenureStartDate();
        this.tenureEndDate = member.getTenureEndDate();
        this.isActive = member.getIsActive();
        this.isProminent = member.getIsProminent();
        this.isCurrentlyServing = member.isCurrentlyServing();
        this.hasTenureExpired = member.hasTenureExpired();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
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

    public Boolean getIsCurrentlyServing() {
        return isCurrentlyServing;
    }

    public void setIsCurrentlyServing(Boolean isCurrentlyServing) {
        this.isCurrentlyServing = isCurrentlyServing;
    }

    public Boolean getHasTenureExpired() {
        return hasTenureExpired;
    }

    public void setHasTenureExpired(Boolean hasTenureExpired) {
        this.hasTenureExpired = hasTenureExpired;
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