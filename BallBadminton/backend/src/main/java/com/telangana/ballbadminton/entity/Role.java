package com.telangana.ballbadminton.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration of user roles in the system
 * Defines the different levels of access and permissions
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public enum Role {
    /**
     * Super administrator with full system access
     */
    SUPER_ADMIN("Super Administrator", "Full system access and management"),
    
    /**
     * Administrator with content management access
     */
    ADMIN("Administrator", "Content management and user administration"),
    
    /**
     * Content editor with limited content management access
     */
    EDITOR("Editor", "Content creation and editing"),
    
    /**
     * Moderator with content review and approval access
     */
    MODERATOR("Moderator", "Content review and moderation"),
    
    /**
     * Regular user with basic access
     */
    USER("User", "Basic user access");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get all permissions associated with this role
     */
    public Set<Permission> getPermissions() {
        return switch (this) {
            case SUPER_ADMIN -> getAllPermissions();
            case ADMIN -> getAdminPermissions();
            case EDITOR -> getEditorPermissions();
            case MODERATOR -> getModeratorPermissions();
            case USER -> getUserPermissions();
        };
    }

    /**
     * Check if this role has a specific permission
     */
    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    /**
     * Check if this role has administrative privileges
     */
    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    /**
     * Check if this role can manage content
     */
    public boolean canManageContent() {
        return this == SUPER_ADMIN || this == ADMIN || this == EDITOR;
    }

    /**
     * Check if this role can moderate content
     */
    public boolean canModerate() {
        return this == SUPER_ADMIN || this == ADMIN || this == MODERATOR;
    }

    /**
     * Get role hierarchy level (higher number = more privileges)
     */
    public int getHierarchyLevel() {
        return switch (this) {
            case SUPER_ADMIN -> 5;
            case ADMIN -> 4;
            case EDITOR -> 3;
            case MODERATOR -> 2;
            case USER -> 1;
        };
    }

    /**
     * Check if this role has higher or equal privileges than another role
     */
    public boolean hasPrivilegeOver(Role other) {
        return this.getHierarchyLevel() >= other.getHierarchyLevel();
    }

    /**
     * Get all permissions in the system (for SUPER_ADMIN)
     */
    private static Set<Permission> getAllPermissions() {
        return Arrays.stream(Permission.values()).collect(Collectors.toSet());
    }

    /**
     * Get permissions for ADMIN role
     */
    private static Set<Permission> getAdminPermissions() {
        return Stream.of(
            Permission.getUserManagementPermissions(),
            Permission.getMemberManagementPermissions(),
            Permission.getPlayerManagementPermissions(),
            Permission.getTournamentManagementPermissions(),
            Permission.getNewsManagementPermissions(),
            Permission.getMediaManagementPermissions(),
            Permission.getDistrictManagementPermissions(),
            Permission.getContentModerationPermissions(),
            Permission.getFileManagementPermissions(),
            new Permission[]{Permission.SYSTEM_MONITOR, Permission.SYSTEM_AUDIT}
        ).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    /**
     * Get permissions for EDITOR role
     */
    private static Set<Permission> getEditorPermissions() {
        return Stream.of(
            new Permission[]{Permission.MEMBER_READ, Permission.MEMBER_UPDATE},
            new Permission[]{Permission.PLAYER_READ, Permission.PLAYER_UPDATE, Permission.PLAYER_MANAGE_STATISTICS, Permission.PLAYER_MANAGE_ACHIEVEMENTS},
            new Permission[]{Permission.TOURNAMENT_READ, Permission.TOURNAMENT_UPDATE, Permission.TOURNAMENT_MANAGE_REGISTRATION},
            Permission.getNewsManagementPermissions(),
            Permission.getMediaManagementPermissions(),
            new Permission[]{Permission.DISTRICT_READ, Permission.DISTRICT_UPDATE},
            new Permission[]{Permission.FILE_UPLOAD, Permission.FILE_DOWNLOAD, Permission.FILE_MANAGE}
        ).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    /**
     * Get permissions for MODERATOR role
     */
    private static Set<Permission> getModeratorPermissions() {
        return Stream.of(
            new Permission[]{Permission.MEMBER_READ},
            new Permission[]{Permission.PLAYER_READ},
            new Permission[]{Permission.TOURNAMENT_READ},
            new Permission[]{Permission.NEWS_READ, Permission.NEWS_MODERATE},
            new Permission[]{Permission.MEDIA_READ},
            new Permission[]{Permission.DISTRICT_READ},
            Permission.getContentModerationPermissions(),
            new Permission[]{Permission.FILE_DOWNLOAD}
        ).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    /**
     * Get permissions for USER role
     */
    private static Set<Permission> getUserPermissions() {
        return Set.of(
            Permission.MEMBER_READ,
            Permission.PLAYER_READ,
            Permission.TOURNAMENT_READ,
            Permission.NEWS_READ,
            Permission.MEDIA_READ,
            Permission.DISTRICT_READ,
            Permission.FILE_DOWNLOAD
        );
    }
}