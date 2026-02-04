package com.telangana.ballbadminton.security;

import com.telangana.ballbadminton.entity.Permission;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthorizationService
 * Tests permission checking, role validation, and authorization logic
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization Service Tests")
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    private User testUser;
    private Authentication mockAuthentication;
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        
        mockAuthentication = mock(Authentication.class);
        mockSecurityContext = mock(SecurityContext.class);
    }

    @Test
    @DisplayName("Should return current user when authenticated")
    void shouldReturnCurrentUserWhenAuthenticated() {
        // Given
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When
            User currentUser = authorizationService.getCurrentUser();

            // Then
            assertThat(currentUser).isEqualTo(testUser);
        }
    }

    @Test
    @DisplayName("Should return null when not authenticated")
    void shouldReturnNullWhenNotAuthenticated() {
        // Given
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When
            User currentUser = authorizationService.getCurrentUser();

            // Then
            assertThat(currentUser).isNull();
        }
    }

    @Test
    @DisplayName("Should check permission correctly for admin user")
    void shouldCheckPermissionCorrectlyForAdminUser() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasPermission(Permission.USER_CREATE)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.MEMBER_READ)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.SYSTEM_ADMIN)).isFalse(); // Only SUPER_ADMIN has this
        }
    }

    @Test
    @DisplayName("Should check permission correctly for editor user")
    void shouldCheckPermissionCorrectlyForEditorUser() {
        // Given
        testUser.setRoles(Set.of(Role.EDITOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasPermission(Permission.NEWS_CREATE)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.MEMBER_READ)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.USER_CREATE)).isFalse(); // Editors can't create users
            assertThat(authorizationService.hasPermission(Permission.MEMBER_DELETE)).isFalse(); // Editors can't delete members
        }
    }

    @Test
    @DisplayName("Should check permission correctly for regular user")
    void shouldCheckPermissionCorrectlyForRegularUser() {
        // Given
        testUser.setRoles(Set.of(Role.USER));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasPermission(Permission.MEMBER_READ)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.PLAYER_READ)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.FILE_DOWNLOAD)).isTrue();
            assertThat(authorizationService.hasPermission(Permission.MEMBER_CREATE)).isFalse();
            assertThat(authorizationService.hasPermission(Permission.USER_CREATE)).isFalse();
        }
    }

    @Test
    @DisplayName("Should check any permission correctly")
    void shouldCheckAnyPermissionCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.MODERATOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasAnyPermission(
                Permission.CONTENT_MODERATE, Permission.USER_CREATE)).isTrue(); // Has CONTENT_MODERATE
            assertThat(authorizationService.hasAnyPermission(
                Permission.USER_CREATE, Permission.MEMBER_DELETE)).isFalse(); // Has neither
        }
    }

    @Test
    @DisplayName("Should check all permissions correctly")
    void shouldCheckAllPermissionsCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasAllPermissions(
                Permission.USER_READ, Permission.MEMBER_READ)).isTrue(); // Has both
            assertThat(authorizationService.hasAllPermissions(
                Permission.USER_READ, Permission.SYSTEM_ADMIN)).isFalse(); // Missing SYSTEM_ADMIN
        }
    }

    @Test
    @DisplayName("Should check role correctly")
    void shouldCheckRoleCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN, Role.EDITOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasRole(Role.ADMIN)).isTrue();
            assertThat(authorizationService.hasRole(Role.EDITOR)).isTrue();
            assertThat(authorizationService.hasRole(Role.SUPER_ADMIN)).isFalse();
        }
    }

    @Test
    @DisplayName("Should check any role correctly")
    void shouldCheckAnyRoleCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.EDITOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.hasAnyRole(Role.ADMIN, Role.EDITOR)).isTrue();
            assertThat(authorizationService.hasAnyRole(Role.ADMIN, Role.SUPER_ADMIN)).isFalse();
        }
    }

    @Test
    @DisplayName("Should identify admin correctly")
    void shouldIdentifyAdminCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.isAdmin()).isTrue();
        }
    }

    @Test
    @DisplayName("Should identify super admin correctly")
    void shouldIdentifySuperAdminCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.SUPER_ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.isAdmin()).isTrue();
        }
    }

    @Test
    @DisplayName("Should identify non-admin correctly")
    void shouldIdentifyNonAdminCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.EDITOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.isAdmin()).isFalse();
        }
    }

    @Test
    @DisplayName("Should allow user to manage themselves")
    void shouldAllowUserToManageThemselves() {
        // Given
        UUID userId = UUID.randomUUID();
        testUser.setId(userId);
        testUser.setRoles(Set.of(Role.USER));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.canManageUser(userId)).isTrue();
        }
    }

    @Test
    @DisplayName("Should allow admin to manage any user")
    void shouldAllowAdminToManageAnyUser() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.canManageUser(UUID.randomUUID())).isTrue();
        }
    }

    @Test
    @DisplayName("Should perform action checks correctly")
    void shouldPerformActionChecksCorrectly() {
        // Given
        testUser.setRoles(Set.of(Role.EDITOR));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThat(authorizationService.canPerformAction("member", "read")).isTrue();
            assertThat(authorizationService.canPerformAction("member", "update")).isTrue();
            assertThat(authorizationService.canPerformAction("member", "delete")).isFalse();
            assertThat(authorizationService.canPerformAction("news", "create")).isTrue();
            assertThat(authorizationService.canPerformAction("user", "create")).isFalse();
        }
    }

    @Test
    @DisplayName("Should require permission and throw exception when missing")
    void shouldRequirePermissionAndThrowExceptionWhenMissing() {
        // Given
        testUser.setRoles(Set.of(Role.USER));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThatThrownBy(() -> authorizationService.requirePermission(Permission.USER_CREATE))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Access denied");
        }
    }

    @Test
    @DisplayName("Should require permission and pass when present")
    void shouldRequirePermissionAndPassWhenPresent() {
        // Given
        testUser.setRoles(Set.of(Role.ADMIN));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then - should not throw exception
            authorizationService.requirePermission(Permission.USER_CREATE);
        }
    }

    @Test
    @DisplayName("Should require role and throw exception when missing")
    void shouldRequireRoleAndThrowExceptionWhenMissing() {
        // Given
        testUser.setRoles(Set.of(Role.USER));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThatThrownBy(() -> authorizationService.requireRole(Role.ADMIN))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Access denied");
        }
    }

    @Test
    @DisplayName("Should require admin and throw exception when not admin")
    void shouldRequireAdminAndThrowExceptionWhenNotAdmin() {
        // Given
        testUser.setRoles(Set.of(Role.USER));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // When & Then
            assertThatThrownBy(() -> authorizationService.requireAdmin())
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Administrative privileges required");
        }
    }
}