package com.telangana.ballbadminton.entity;

/**
 * Enumeration of system permissions for fine-grained access control
 * Defines specific actions that can be performed by users with different roles
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public enum Permission {
    // User Management Permissions
    USER_CREATE("Create new users", "Ability to create new user accounts"),
    USER_READ("View users", "Ability to view user information"),
    USER_UPDATE("Update users", "Ability to update user information"),
    USER_DELETE("Delete users", "Ability to delete user accounts"),
    USER_MANAGE_ROLES("Manage user roles", "Ability to assign and remove user roles"),
    
    // Member Management Permissions
    MEMBER_CREATE("Create members", "Ability to create new association members"),
    MEMBER_READ("View members", "Ability to view member information"),
    MEMBER_UPDATE("Update members", "Ability to update member information"),
    MEMBER_DELETE("Delete members", "Ability to delete member records"),
    MEMBER_MANAGE_HIERARCHY("Manage member hierarchy", "Ability to manage organizational hierarchy"),
    
    // Player Management Permissions
    PLAYER_CREATE("Create players", "Ability to create new player profiles"),
    PLAYER_READ("View players", "Ability to view player information"),
    PLAYER_UPDATE("Update players", "Ability to update player information"),
    PLAYER_DELETE("Delete players", "Ability to delete player records"),
    PLAYER_MANAGE_STATISTICS("Manage player statistics", "Ability to update player statistics"),
    PLAYER_MANAGE_ACHIEVEMENTS("Manage player achievements", "Ability to manage player achievements"),
    
    // Tournament Management Permissions
    TOURNAMENT_CREATE("Create tournaments", "Ability to create new tournaments"),
    TOURNAMENT_READ("View tournaments", "Ability to view tournament information"),
    TOURNAMENT_UPDATE("Update tournaments", "Ability to update tournament information"),
    TOURNAMENT_DELETE("Delete tournaments", "Ability to delete tournament records"),
    TOURNAMENT_MANAGE_REGISTRATION("Manage tournament registration", "Ability to manage tournament registrations"),
    TOURNAMENT_MANAGE_RESULTS("Manage tournament results", "Ability to update tournament results"),
    
    // News and Content Management Permissions
    NEWS_CREATE("Create news", "Ability to create news articles"),
    NEWS_READ("View news", "Ability to view news articles"),
    NEWS_UPDATE("Update news", "Ability to update news articles"),
    NEWS_DELETE("Delete news", "Ability to delete news articles"),
    NEWS_PUBLISH("Publish news", "Ability to publish news articles"),
    NEWS_MODERATE("Moderate news", "Ability to moderate and approve news content"),
    
    // Media Management Permissions
    MEDIA_CREATE("Upload media", "Ability to upload media files"),
    MEDIA_READ("View media", "Ability to view media galleries"),
    MEDIA_UPDATE("Update media", "Ability to update media information"),
    MEDIA_DELETE("Delete media", "Ability to delete media files"),
    MEDIA_MANAGE_GALLERIES("Manage media galleries", "Ability to organize media galleries"),
    
    // District Management Permissions
    DISTRICT_CREATE("Create districts", "Ability to create district information"),
    DISTRICT_READ("View districts", "Ability to view district information"),
    DISTRICT_UPDATE("Update districts", "Ability to update district information"),
    DISTRICT_DELETE("Delete districts", "Ability to delete district records"),
    DISTRICT_MANAGE_STATISTICS("Manage district statistics", "Ability to update district statistics"),
    
    // System Administration Permissions
    SYSTEM_ADMIN("System administration", "Full system administration access"),
    SYSTEM_BACKUP("System backup", "Ability to perform system backups"),
    SYSTEM_RESTORE("System restore", "Ability to restore system from backups"),
    SYSTEM_MONITOR("System monitoring", "Ability to monitor system health and performance"),
    SYSTEM_AUDIT("System audit", "Ability to view system audit logs"),
    
    // Content Moderation Permissions
    CONTENT_MODERATE("Moderate content", "Ability to moderate user-generated content"),
    CONTENT_APPROVE("Approve content", "Ability to approve content for publication"),
    CONTENT_REJECT("Reject content", "Ability to reject content submissions"),
    
    // File Management Permissions
    FILE_UPLOAD("Upload files", "Ability to upload files to the system"),
    FILE_DOWNLOAD("Download files", "Ability to download files from the system"),
    FILE_DELETE("Delete files", "Ability to delete files from the system"),
    FILE_MANAGE("Manage files", "Ability to organize and manage file structure");

    private final String displayName;
    private final String description;

    Permission(String displayName, String description) {
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
     * Get the Spring Security authority name for this permission
     */
    public String getAuthority() {
        return "PERMISSION_" + this.name();
    }

    /**
     * Get permissions by category for easier management
     */
    public static Permission[] getUserManagementPermissions() {
        return new Permission[]{
            USER_CREATE, USER_READ, USER_UPDATE, USER_DELETE, USER_MANAGE_ROLES
        };
    }

    public static Permission[] getMemberManagementPermissions() {
        return new Permission[]{
            MEMBER_CREATE, MEMBER_READ, MEMBER_UPDATE, MEMBER_DELETE, MEMBER_MANAGE_HIERARCHY
        };
    }

    public static Permission[] getPlayerManagementPermissions() {
        return new Permission[]{
            PLAYER_CREATE, PLAYER_READ, PLAYER_UPDATE, PLAYER_DELETE, 
            PLAYER_MANAGE_STATISTICS, PLAYER_MANAGE_ACHIEVEMENTS
        };
    }

    public static Permission[] getTournamentManagementPermissions() {
        return new Permission[]{
            TOURNAMENT_CREATE, TOURNAMENT_READ, TOURNAMENT_UPDATE, TOURNAMENT_DELETE,
            TOURNAMENT_MANAGE_REGISTRATION, TOURNAMENT_MANAGE_RESULTS
        };
    }

    public static Permission[] getNewsManagementPermissions() {
        return new Permission[]{
            NEWS_CREATE, NEWS_READ, NEWS_UPDATE, NEWS_DELETE, NEWS_PUBLISH, NEWS_MODERATE
        };
    }

    public static Permission[] getMediaManagementPermissions() {
        return new Permission[]{
            MEDIA_CREATE, MEDIA_READ, MEDIA_UPDATE, MEDIA_DELETE, MEDIA_MANAGE_GALLERIES
        };
    }

    public static Permission[] getDistrictManagementPermissions() {
        return new Permission[]{
            DISTRICT_CREATE, DISTRICT_READ, DISTRICT_UPDATE, DISTRICT_DELETE, DISTRICT_MANAGE_STATISTICS
        };
    }

    public static Permission[] getSystemAdministrationPermissions() {
        return new Permission[]{
            SYSTEM_ADMIN, SYSTEM_BACKUP, SYSTEM_RESTORE, SYSTEM_MONITOR, SYSTEM_AUDIT
        };
    }

    public static Permission[] getContentModerationPermissions() {
        return new Permission[]{
            CONTENT_MODERATE, CONTENT_APPROVE, CONTENT_REJECT
        };
    }

    public static Permission[] getFileManagementPermissions() {
        return new Permission[]{
            FILE_UPLOAD, FILE_DOWNLOAD, FILE_DELETE, FILE_MANAGE
        };
    }
}