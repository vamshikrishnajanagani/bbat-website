package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Permission enum
 * Tests permission categories, authority names, and utility methods
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Permission Entity Tests")
class PermissionTest {

    @Test
    @DisplayName("Should have correct authority names")
    void shouldHaveCorrectAuthorityNames() {
        assertThat(Permission.USER_CREATE.getAuthority()).isEqualTo("PERMISSION_USER_CREATE");
        assertThat(Permission.MEMBER_READ.getAuthority()).isEqualTo("PERMISSION_MEMBER_READ");
        assertThat(Permission.SYSTEM_ADMIN.getAuthority()).isEqualTo("PERMISSION_SYSTEM_ADMIN");
    }

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(Permission.USER_CREATE.getDisplayName()).isEqualTo("Create new users");
        assertThat(Permission.MEMBER_READ.getDisplayName()).isEqualTo("View members");
        assertThat(Permission.SYSTEM_ADMIN.getDisplayName()).isEqualTo("System administration");
    }

    @Test
    @DisplayName("Should have correct descriptions")
    void shouldHaveCorrectDescriptions() {
        assertThat(Permission.USER_CREATE.getDescription()).isEqualTo("Ability to create new user accounts");
        assertThat(Permission.MEMBER_READ.getDescription()).isEqualTo("Ability to view member information");
        assertThat(Permission.SYSTEM_ADMIN.getDescription()).isEqualTo("Full system administration access");
    }

    @Test
    @DisplayName("Should return correct user management permissions")
    void shouldReturnCorrectUserManagementPermissions() {
        Permission[] permissions = Permission.getUserManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.USER_CREATE,
            Permission.USER_READ,
            Permission.USER_UPDATE,
            Permission.USER_DELETE,
            Permission.USER_MANAGE_ROLES
        );
    }

    @Test
    @DisplayName("Should return correct member management permissions")
    void shouldReturnCorrectMemberManagementPermissions() {
        Permission[] permissions = Permission.getMemberManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.MEMBER_CREATE,
            Permission.MEMBER_READ,
            Permission.MEMBER_UPDATE,
            Permission.MEMBER_DELETE,
            Permission.MEMBER_MANAGE_HIERARCHY
        );
    }

    @Test
    @DisplayName("Should return correct player management permissions")
    void shouldReturnCorrectPlayerManagementPermissions() {
        Permission[] permissions = Permission.getPlayerManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.PLAYER_CREATE,
            Permission.PLAYER_READ,
            Permission.PLAYER_UPDATE,
            Permission.PLAYER_DELETE,
            Permission.PLAYER_MANAGE_STATISTICS,
            Permission.PLAYER_MANAGE_ACHIEVEMENTS
        );
    }

    @Test
    @DisplayName("Should return correct tournament management permissions")
    void shouldReturnCorrectTournamentManagementPermissions() {
        Permission[] permissions = Permission.getTournamentManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.TOURNAMENT_CREATE,
            Permission.TOURNAMENT_READ,
            Permission.TOURNAMENT_UPDATE,
            Permission.TOURNAMENT_DELETE,
            Permission.TOURNAMENT_MANAGE_REGISTRATION,
            Permission.TOURNAMENT_MANAGE_RESULTS
        );
    }

    @Test
    @DisplayName("Should return correct news management permissions")
    void shouldReturnCorrectNewsManagementPermissions() {
        Permission[] permissions = Permission.getNewsManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.NEWS_CREATE,
            Permission.NEWS_READ,
            Permission.NEWS_UPDATE,
            Permission.NEWS_DELETE,
            Permission.NEWS_PUBLISH,
            Permission.NEWS_MODERATE
        );
    }

    @Test
    @DisplayName("Should return correct media management permissions")
    void shouldReturnCorrectMediaManagementPermissions() {
        Permission[] permissions = Permission.getMediaManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.MEDIA_CREATE,
            Permission.MEDIA_READ,
            Permission.MEDIA_UPDATE,
            Permission.MEDIA_DELETE,
            Permission.MEDIA_MANAGE_GALLERIES
        );
    }

    @Test
    @DisplayName("Should return correct district management permissions")
    void shouldReturnCorrectDistrictManagementPermissions() {
        Permission[] permissions = Permission.getDistrictManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.DISTRICT_CREATE,
            Permission.DISTRICT_READ,
            Permission.DISTRICT_UPDATE,
            Permission.DISTRICT_DELETE,
            Permission.DISTRICT_MANAGE_STATISTICS
        );
    }

    @Test
    @DisplayName("Should return correct system administration permissions")
    void shouldReturnCorrectSystemAdministrationPermissions() {
        Permission[] permissions = Permission.getSystemAdministrationPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.SYSTEM_ADMIN,
            Permission.SYSTEM_BACKUP,
            Permission.SYSTEM_RESTORE,
            Permission.SYSTEM_MONITOR,
            Permission.SYSTEM_AUDIT
        );
    }

    @Test
    @DisplayName("Should return correct content moderation permissions")
    void shouldReturnCorrectContentModerationPermissions() {
        Permission[] permissions = Permission.getContentModerationPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.CONTENT_MODERATE,
            Permission.CONTENT_APPROVE,
            Permission.CONTENT_REJECT
        );
    }

    @Test
    @DisplayName("Should return correct file management permissions")
    void shouldReturnCorrectFileManagementPermissions() {
        Permission[] permissions = Permission.getFileManagementPermissions();
        
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.FILE_UPLOAD,
            Permission.FILE_DOWNLOAD,
            Permission.FILE_DELETE,
            Permission.FILE_MANAGE
        );
    }

    @Test
    @DisplayName("Should have all expected permission categories")
    void shouldHaveAllExpectedPermissionCategories() {
        // Verify that we have permissions for all major categories
        assertThat(Permission.getUserManagementPermissions()).isNotEmpty();
        assertThat(Permission.getMemberManagementPermissions()).isNotEmpty();
        assertThat(Permission.getPlayerManagementPermissions()).isNotEmpty();
        assertThat(Permission.getTournamentManagementPermissions()).isNotEmpty();
        assertThat(Permission.getNewsManagementPermissions()).isNotEmpty();
        assertThat(Permission.getMediaManagementPermissions()).isNotEmpty();
        assertThat(Permission.getDistrictManagementPermissions()).isNotEmpty();
        assertThat(Permission.getSystemAdministrationPermissions()).isNotEmpty();
        assertThat(Permission.getContentModerationPermissions()).isNotEmpty();
        assertThat(Permission.getFileManagementPermissions()).isNotEmpty();
    }

    @Test
    @DisplayName("Should have unique permission names")
    void shouldHaveUniquePermissionNames() {
        Permission[] allPermissions = Permission.values();
        
        // Check that all permission names are unique
        assertThat(allPermissions).doesNotHaveDuplicates();
        
        // Check that all authority names are unique
        String[] authorities = new String[allPermissions.length];
        for (int i = 0; i < allPermissions.length; i++) {
            authorities[i] = allPermissions[i].getAuthority();
        }
        assertThat(authorities).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Should follow naming conventions")
    void shouldFollowNamingConventions() {
        for (Permission permission : Permission.values()) {
            // All permission names should be uppercase with underscores
            assertThat(permission.name()).matches("^[A-Z_]+$");
            
            // All authority names should start with PERMISSION_
            assertThat(permission.getAuthority()).startsWith("PERMISSION_");
            
            // Display names should not be empty
            assertThat(permission.getDisplayName()).isNotBlank();
            
            // Descriptions should not be empty
            assertThat(permission.getDescription()).isNotBlank();
        }
    }
}