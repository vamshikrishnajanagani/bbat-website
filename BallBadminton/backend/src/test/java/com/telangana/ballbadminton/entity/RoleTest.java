package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Role enum
 * Tests role hierarchy, permissions, and utility methods
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Role Entity Tests")
class RoleTest {

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(Role.SUPER_ADMIN.getDisplayName()).isEqualTo("Super Administrator");
        assertThat(Role.ADMIN.getDisplayName()).isEqualTo("Administrator");
        assertThat(Role.EDITOR.getDisplayName()).isEqualTo("Editor");
        assertThat(Role.MODERATOR.getDisplayName()).isEqualTo("Moderator");
        assertThat(Role.USER.getDisplayName()).isEqualTo("User");
    }

    @Test
    @DisplayName("Should have correct descriptions")
    void shouldHaveCorrectDescriptions() {
        assertThat(Role.SUPER_ADMIN.getDescription()).isEqualTo("Full system access and management");
        assertThat(Role.ADMIN.getDescription()).isEqualTo("Content management and user administration");
        assertThat(Role.EDITOR.getDescription()).isEqualTo("Content creation and editing");
        assertThat(Role.MODERATOR.getDescription()).isEqualTo("Content review and moderation");
        assertThat(Role.USER.getDescription()).isEqualTo("Basic user access");
    }

    @Test
    @DisplayName("Should have correct hierarchy levels")
    void shouldHaveCorrectHierarchyLevels() {
        assertThat(Role.SUPER_ADMIN.getHierarchyLevel()).isEqualTo(5);
        assertThat(Role.ADMIN.getHierarchyLevel()).isEqualTo(4);
        assertThat(Role.EDITOR.getHierarchyLevel()).isEqualTo(3);
        assertThat(Role.MODERATOR.getHierarchyLevel()).isEqualTo(2);
        assertThat(Role.USER.getHierarchyLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should identify admin roles correctly")
    void shouldIdentifyAdminRolesCorrectly() {
        assertThat(Role.SUPER_ADMIN.isAdmin()).isTrue();
        assertThat(Role.ADMIN.isAdmin()).isTrue();
        assertThat(Role.EDITOR.isAdmin()).isFalse();
        assertThat(Role.MODERATOR.isAdmin()).isFalse();
        assertThat(Role.USER.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should identify content management roles correctly")
    void shouldIdentifyContentManagementRolesCorrectly() {
        assertThat(Role.SUPER_ADMIN.canManageContent()).isTrue();
        assertThat(Role.ADMIN.canManageContent()).isTrue();
        assertThat(Role.EDITOR.canManageContent()).isTrue();
        assertThat(Role.MODERATOR.canManageContent()).isFalse();
        assertThat(Role.USER.canManageContent()).isFalse();
    }

    @Test
    @DisplayName("Should identify moderation roles correctly")
    void shouldIdentifyModerationRolesCorrectly() {
        assertThat(Role.SUPER_ADMIN.canModerate()).isTrue();
        assertThat(Role.ADMIN.canModerate()).isTrue();
        assertThat(Role.MODERATOR.canModerate()).isTrue();
        assertThat(Role.EDITOR.canModerate()).isFalse();
        assertThat(Role.USER.canModerate()).isFalse();
    }

    @Test
    @DisplayName("Should check privilege hierarchy correctly")
    void shouldCheckPrivilegeHierarchyCorrectly() {
        // Super admin has privilege over everyone
        assertThat(Role.SUPER_ADMIN.hasPrivilegeOver(Role.ADMIN)).isTrue();
        assertThat(Role.SUPER_ADMIN.hasPrivilegeOver(Role.EDITOR)).isTrue();
        assertThat(Role.SUPER_ADMIN.hasPrivilegeOver(Role.MODERATOR)).isTrue();
        assertThat(Role.SUPER_ADMIN.hasPrivilegeOver(Role.USER)).isTrue();
        assertThat(Role.SUPER_ADMIN.hasPrivilegeOver(Role.SUPER_ADMIN)).isTrue();

        // Admin has privilege over lower roles
        assertThat(Role.ADMIN.hasPrivilegeOver(Role.EDITOR)).isTrue();
        assertThat(Role.ADMIN.hasPrivilegeOver(Role.MODERATOR)).isTrue();
        assertThat(Role.ADMIN.hasPrivilegeOver(Role.USER)).isTrue();
        assertThat(Role.ADMIN.hasPrivilegeOver(Role.ADMIN)).isTrue();
        assertThat(Role.ADMIN.hasPrivilegeOver(Role.SUPER_ADMIN)).isFalse();

        // Editor has privilege over lower roles
        assertThat(Role.EDITOR.hasPrivilegeOver(Role.MODERATOR)).isTrue();
        assertThat(Role.EDITOR.hasPrivilegeOver(Role.USER)).isTrue();
        assertThat(Role.EDITOR.hasPrivilegeOver(Role.EDITOR)).isTrue();
        assertThat(Role.EDITOR.hasPrivilegeOver(Role.ADMIN)).isFalse();

        // User has no privilege over others
        assertThat(Role.USER.hasPrivilegeOver(Role.MODERATOR)).isFalse();
        assertThat(Role.USER.hasPrivilegeOver(Role.EDITOR)).isFalse();
        assertThat(Role.USER.hasPrivilegeOver(Role.ADMIN)).isFalse();
        assertThat(Role.USER.hasPrivilegeOver(Role.SUPER_ADMIN)).isFalse();
        assertThat(Role.USER.hasPrivilegeOver(Role.USER)).isTrue();
    }

    @Test
    @DisplayName("Should have correct permissions for SUPER_ADMIN")
    void shouldHaveCorrectPermissionsForSuperAdmin() {
        Set<Permission> permissions = Role.SUPER_ADMIN.getPermissions();
        
        // Super admin should have all permissions
        assertThat(permissions).containsAll(Set.of(Permission.values()));
        assertThat(permissions).hasSize(Permission.values().length);
    }

    @Test
    @DisplayName("Should have correct permissions for ADMIN")
    void shouldHaveCorrectPermissionsForAdmin() {
        Set<Permission> permissions = Role.ADMIN.getPermissions();
        
        // Admin should have most permissions except system admin ones
        assertThat(permissions).contains(
            Permission.USER_CREATE, Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_DELETE,
            Permission.MEMBER_CREATE, Permission.MEMBER_READ, Permission.MEMBER_UPDATE, Permission.MEMBER_DELETE,
            Permission.PLAYER_CREATE, Permission.PLAYER_READ, Permission.PLAYER_UPDATE, Permission.PLAYER_DELETE,
            Permission.TOURNAMENT_CREATE, Permission.TOURNAMENT_READ, Permission.TOURNAMENT_UPDATE, Permission.TOURNAMENT_DELETE,
            Permission.NEWS_CREATE, Permission.NEWS_READ, Permission.NEWS_UPDATE, Permission.NEWS_DELETE,
            Permission.MEDIA_CREATE, Permission.MEDIA_READ, Permission.MEDIA_UPDATE, Permission.MEDIA_DELETE,
            Permission.DISTRICT_CREATE, Permission.DISTRICT_READ, Permission.DISTRICT_UPDATE, Permission.DISTRICT_DELETE
        );
        
        // Admin should not have system admin permissions
        assertThat(permissions).doesNotContain(
            Permission.SYSTEM_ADMIN, Permission.SYSTEM_BACKUP, Permission.SYSTEM_RESTORE
        );
        
        // But should have monitoring permissions
        assertThat(permissions).contains(Permission.SYSTEM_MONITOR, Permission.SYSTEM_AUDIT);
    }

    @Test
    @DisplayName("Should have correct permissions for EDITOR")
    void shouldHaveCorrectPermissionsForEditor() {
        Set<Permission> permissions = Role.EDITOR.getPermissions();
        
        // Editor should have content management permissions
        assertThat(permissions).contains(
            Permission.MEMBER_READ, Permission.MEMBER_UPDATE,
            Permission.PLAYER_READ, Permission.PLAYER_UPDATE, Permission.PLAYER_MANAGE_STATISTICS,
            Permission.TOURNAMENT_READ, Permission.TOURNAMENT_UPDATE,
            Permission.NEWS_CREATE, Permission.NEWS_READ, Permission.NEWS_UPDATE, Permission.NEWS_DELETE,
            Permission.MEDIA_CREATE, Permission.MEDIA_READ, Permission.MEDIA_UPDATE, Permission.MEDIA_DELETE,
            Permission.DISTRICT_READ, Permission.DISTRICT_UPDATE
        );
        
        // Editor should not have user management or deletion permissions
        assertThat(permissions).doesNotContain(
            Permission.USER_CREATE, Permission.USER_DELETE,
            Permission.MEMBER_CREATE, Permission.MEMBER_DELETE,
            Permission.PLAYER_CREATE, Permission.PLAYER_DELETE,
            Permission.TOURNAMENT_CREATE, Permission.TOURNAMENT_DELETE
        );
    }

    @Test
    @DisplayName("Should have correct permissions for MODERATOR")
    void shouldHaveCorrectPermissionsForModerator() {
        Set<Permission> permissions = Role.MODERATOR.getPermissions();
        
        // Moderator should have read permissions and moderation permissions
        assertThat(permissions).contains(
            Permission.MEMBER_READ,
            Permission.PLAYER_READ,
            Permission.TOURNAMENT_READ,
            Permission.NEWS_READ, Permission.NEWS_MODERATE,
            Permission.MEDIA_READ,
            Permission.DISTRICT_READ,
            Permission.CONTENT_MODERATE, Permission.CONTENT_APPROVE, Permission.CONTENT_REJECT
        );
        
        // Moderator should not have create/update/delete permissions
        assertThat(permissions).doesNotContain(
            Permission.MEMBER_CREATE, Permission.MEMBER_UPDATE, Permission.MEMBER_DELETE,
            Permission.PLAYER_CREATE, Permission.PLAYER_UPDATE, Permission.PLAYER_DELETE,
            Permission.NEWS_CREATE, Permission.NEWS_UPDATE, Permission.NEWS_DELETE
        );
    }

    @Test
    @DisplayName("Should have correct permissions for USER")
    void shouldHaveCorrectPermissionsForUser() {
        Set<Permission> permissions = Role.USER.getPermissions();
        
        // User should only have read permissions
        assertThat(permissions).containsExactlyInAnyOrder(
            Permission.MEMBER_READ,
            Permission.PLAYER_READ,
            Permission.TOURNAMENT_READ,
            Permission.NEWS_READ,
            Permission.MEDIA_READ,
            Permission.DISTRICT_READ,
            Permission.FILE_DOWNLOAD
        );
        
        // User should not have any create/update/delete permissions
        assertThat(permissions).doesNotContain(
            Permission.MEMBER_CREATE, Permission.MEMBER_UPDATE, Permission.MEMBER_DELETE,
            Permission.PLAYER_CREATE, Permission.PLAYER_UPDATE, Permission.PLAYER_DELETE,
            Permission.USER_CREATE, Permission.USER_UPDATE, Permission.USER_DELETE
        );
    }

    @Test
    @DisplayName("Should check specific permissions correctly")
    void shouldCheckSpecificPermissionsCorrectly() {
        // Test specific permission checks
        assertThat(Role.SUPER_ADMIN.hasPermission(Permission.SYSTEM_ADMIN)).isTrue();
        assertThat(Role.ADMIN.hasPermission(Permission.USER_CREATE)).isTrue();
        assertThat(Role.ADMIN.hasPermission(Permission.SYSTEM_ADMIN)).isFalse();
        assertThat(Role.EDITOR.hasPermission(Permission.NEWS_CREATE)).isTrue();
        assertThat(Role.EDITOR.hasPermission(Permission.USER_CREATE)).isFalse();
        assertThat(Role.MODERATOR.hasPermission(Permission.CONTENT_MODERATE)).isTrue();
        assertThat(Role.MODERATOR.hasPermission(Permission.NEWS_CREATE)).isFalse();
        assertThat(Role.USER.hasPermission(Permission.MEMBER_READ)).isTrue();
        assertThat(Role.USER.hasPermission(Permission.MEMBER_CREATE)).isFalse();
    }
}