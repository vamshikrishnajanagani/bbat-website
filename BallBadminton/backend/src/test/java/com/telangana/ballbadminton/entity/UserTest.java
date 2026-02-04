package com.telangana.ballbadminton.entity;

import com.telangana.ballbadminton.base.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity
 * Tests entity functionality, validation, and UserDetails implementation
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("User Entity Tests")
class UserTest extends BaseUnitTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("1234567890");
        user.setRoles(Set.of(Role.USER));
    }

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        // Given - user created in setUp()
        
        // When & Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getRoles()).containsExactly(Role.USER);
        assertThat(user.getActive()).isTrue();
        assertThat(user.getAccountNonExpired()).isTrue();
        assertThat(user.getAccountNonLocked()).isTrue();
        assertThat(user.getCredentialsNonExpired()).isTrue();
        assertThat(user.getEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should create user with constructor")
    void shouldCreateUserWithConstructor() {
        // When
        User newUser = new User("john", "john@example.com", "pass123", "John", "Doe");
        
        // Then
        assertThat(newUser.getUsername()).isEqualTo("john");
        assertThat(newUser.getEmail()).isEqualTo("john@example.com");
        assertThat(newUser.getPassword()).isEqualTo("pass123");
        assertThat(newUser.getFirstName()).isEqualTo("John");
        assertThat(newUser.getLastName()).isEqualTo("Doe");
        assertThat(newUser.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should implement UserDetails correctly")
    void shouldImplementUserDetailsCorrectly() {
        // Given
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        
        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        // Then
        // Should include both role authorities and permission authorities
        assertThat(authorities).hasSizeGreaterThan(2); // More than just the 2 roles
        
        // Check that role authorities are present
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .contains("ROLE_ADMIN", "ROLE_USER");
        
        // Check that some permission authorities are present
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .anyMatch(auth -> auth.startsWith("PERMISSION_"));
        
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle disabled user correctly")
    void shouldHandleDisabledUserCorrectly() {
        // Given
        user.setActive(false);
        
        // When & Then
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should handle expired account correctly")
    void shouldHandleExpiredAccountCorrectly() {
        // Given
        user.setAccountNonExpired(false);
        
        // When & Then
        assertThat(user.isAccountNonExpired()).isFalse();
    }

    @Test
    @DisplayName("Should handle locked account correctly")
    void shouldHandleLockedAccountCorrectly() {
        // Given
        user.setAccountNonLocked(false);
        
        // When & Then
        assertThat(user.isAccountNonLocked()).isFalse();
    }

    @Test
    @DisplayName("Should handle expired credentials correctly")
    void shouldHandleExpiredCredentialsCorrectly() {
        // Given
        user.setCredentialsNonExpired(false);
        
        // When & Then
        assertThat(user.isCredentialsNonExpired()).isFalse();
    }

    @Test
    @DisplayName("Should return full name correctly")
    void shouldReturnFullNameCorrectly() {
        // When
        String fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should check role membership correctly")
    void shouldCheckRoleMembershipCorrectly() {
        // Given
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        
        // When & Then
        assertThat(user.hasRole(Role.ADMIN)).isTrue();
        assertThat(user.hasRole(Role.USER)).isTrue();
        assertThat(user.hasRole(Role.SUPER_ADMIN)).isFalse();
        assertThat(user.hasRole(Role.EDITOR)).isFalse();
    }

    @Test
    @DisplayName("Should identify admin users correctly")
    void shouldIdentifyAdminUsersCorrectly() {
        // Test ADMIN role
        user.setRoles(Set.of(Role.ADMIN));
        assertThat(user.isAdmin()).isTrue();
        
        // Test SUPER_ADMIN role
        user.setRoles(Set.of(Role.SUPER_ADMIN));
        assertThat(user.isAdmin()).isTrue();
        
        // Test non-admin roles
        user.setRoles(Set.of(Role.USER));
        assertThat(user.isAdmin()).isFalse();
        
        user.setRoles(Set.of(Role.EDITOR));
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should handle null roles gracefully")
    void shouldHandleNullRolesGracefully() {
        // Given
        user.setRoles(null);
        
        // When & Then
        assertThat(user.hasRole(Role.USER)).isFalse();
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty roles gracefully")
    void shouldHandleEmptyRolesGracefully() {
        // Given
        user.setRoles(Set.of());
        
        // When & Then
        assertThat(user.hasRole(Role.USER)).isFalse();
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Should update timestamps correctly")
    void shouldUpdateTimestampsCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        user.setLastLogin(now);
        user.setPasswordChangedAt(now);
        
        // Then
        assertThat(user.getLastLogin()).isEqualTo(now);
        assertThat(user.getPasswordChangedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle email verification status")
    void shouldHandleEmailVerificationStatus() {
        // Initially false
        assertThat(user.getEmailVerified()).isFalse();
        
        // Set to true
        user.setEmailVerified(true);
        assertThat(user.getEmailVerified()).isTrue();
        
        // Set back to false
        user.setEmailVerified(false);
        assertThat(user.getEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should generate meaningful toString")
    void shouldGenerateMeaningfulToString() {
        // When
        String toString = user.toString();
        
        // Then
        assertThat(toString).contains("User{");
        assertThat(toString).contains("username='testuser'");
        assertThat(toString).contains("email='test@example.com'");
        assertThat(toString).contains("firstName='Test'");
        assertThat(toString).contains("lastName='User'");
        assertThat(toString).contains("active=true");
        assertThat(toString).contains("roles=[USER]");
    }

    @Test
    @DisplayName("Should check permissions correctly")
    void shouldCheckPermissionsCorrectly() {
        // Given - user with EDITOR role
        user.setRoles(Set.of(Role.EDITOR));
        
        // When & Then
        assertThat(user.hasPermission(Permission.NEWS_CREATE)).isTrue();
        assertThat(user.hasPermission(Permission.MEMBER_READ)).isTrue();
        assertThat(user.hasPermission(Permission.USER_CREATE)).isFalse();
        assertThat(user.hasPermission(Permission.SYSTEM_ADMIN)).isFalse();
    }

    @Test
    @DisplayName("Should check any permission correctly")
    void shouldCheckAnyPermissionCorrectly() {
        // Given - user with MODERATOR role
        user.setRoles(Set.of(Role.MODERATOR));
        
        // When & Then
        assertThat(user.hasAnyPermission(Permission.CONTENT_MODERATE, Permission.USER_CREATE)).isTrue();
        assertThat(user.hasAnyPermission(Permission.USER_CREATE, Permission.SYSTEM_ADMIN)).isFalse();
    }

    @Test
    @DisplayName("Should check all permissions correctly")
    void shouldCheckAllPermissionsCorrectly() {
        // Given - user with ADMIN role
        user.setRoles(Set.of(Role.ADMIN));
        
        // When & Then
        assertThat(user.hasAllPermissions(Permission.USER_READ, Permission.MEMBER_READ)).isTrue();
        assertThat(user.hasAllPermissions(Permission.USER_READ, Permission.SYSTEM_ADMIN)).isFalse();
    }

    @Test
    @DisplayName("Should get all permissions correctly")
    void shouldGetAllPermissionsCorrectly() {
        // Given - user with USER role
        user.setRoles(Set.of(Role.USER));
        
        // When
        Set<Permission> permissions = user.getAllPermissions();
        
        // Then
        assertThat(permissions).contains(
            Permission.MEMBER_READ,
            Permission.PLAYER_READ,
            Permission.TOURNAMENT_READ,
            Permission.NEWS_READ,
            Permission.MEDIA_READ,
            Permission.DISTRICT_READ,
            Permission.FILE_DOWNLOAD
        );
        assertThat(permissions).doesNotContain(
            Permission.USER_CREATE,
            Permission.MEMBER_CREATE,
            Permission.SYSTEM_ADMIN
        );
    }

    @Test
    @DisplayName("Should check user management permissions correctly")
    void shouldCheckUserManagementPermissionsCorrectly() {
        // Create target user
        User targetUser = new User();
        targetUser.setRoles(Set.of(Role.USER));
        
        // Test SUPER_ADMIN can manage anyone
        user.setRoles(Set.of(Role.SUPER_ADMIN));
        assertThat(user.canManageUser(targetUser)).isTrue();
        
        // Test ADMIN can manage lower roles
        user.setRoles(Set.of(Role.ADMIN));
        assertThat(user.canManageUser(targetUser)).isTrue();
        
        // Test USER cannot manage others
        user.setRoles(Set.of(Role.USER));
        assertThat(user.canManageUser(targetUser)).isFalse();
        
        // Test null target user
        assertThat(user.canManageUser(null)).isFalse();
    }

    @Test
    @DisplayName("Should handle permission checks with null roles")
    void shouldHandlePermissionChecksWithNullRoles() {
        // Given
        user.setRoles(null);
        
        // When & Then
        assertThat(user.hasPermission(Permission.USER_READ)).isFalse();
        assertThat(user.hasAnyPermission(Permission.USER_READ, Permission.MEMBER_READ)).isFalse();
        assertThat(user.hasAllPermissions(Permission.USER_READ)).isFalse();
        assertThat(user.getAllPermissions()).isEmpty();
    }

    @Test
    @DisplayName("Should handle permission checks with empty roles")
    void shouldHandlePermissionChecksWithEmptyRoles() {
        // Given
        user.setRoles(Set.of());
        
        // When & Then
        assertThat(user.hasPermission(Permission.USER_READ)).isFalse();
        assertThat(user.hasAnyPermission(Permission.USER_READ, Permission.MEMBER_READ)).isFalse();
        assertThat(user.hasAllPermissions()).isTrue(); // Empty array should return true
        assertThat(user.getAllPermissions()).isEmpty();
    }
}