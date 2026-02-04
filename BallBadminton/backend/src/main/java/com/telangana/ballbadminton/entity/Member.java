package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Entity representing an association member
 * Contains member profile information and organizational hierarchy
 */
@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_members_active", columnList = "is_active"),
    @Index(name = "idx_members_prominent", columnList = "is_prominent"),
    @Index(name = "idx_members_hierarchy", columnList = "hierarchy_level")
})
public class Member extends BaseEntity {

    @NotBlank(message = "Member name is required")
    @Size(max = 100, message = "Member name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    @Column(name = "position", nullable = false, length = 100)
    private String position;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "hierarchy_level")
    private Integer hierarchyLevel = 0;

    @Column(name = "tenure_start_date")
    private LocalDate tenureStartDate;

    @Column(name = "tenure_end_date")
    private LocalDate tenureEndDate;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(name = "is_prominent", nullable = false)
    private Boolean isProminent = false;

    // Constructors
    public Member() {}

    public Member(String name, String position) {
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

    // Helper methods
    public boolean isCurrentlyServing() {
        LocalDate now = LocalDate.now();
        return (isActive != null && isActive) && 
               (tenureStartDate == null || !tenureStartDate.isAfter(now)) &&
               (tenureEndDate == null || !tenureEndDate.isBefore(now));
    }

    public boolean hasTenureExpired() {
        return tenureEndDate != null && tenureEndDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", hierarchyLevel=" + hierarchyLevel +
                ", isActive=" + isActive +
                '}';
    }
}