package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.entity.Permission;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import com.telangana.ballbadminton.security.AuthorizationService;
import com.telangana.ballbadminton.security.annotation.RequireAdmin;
import com.telangana.ballbadminton.security.annotation.RequirePermission;
import com.telangana.ballbadminton.security.annotation.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Controller for user management operations with role-based authorization
 * Demonstrates the implementation of permission-based access control
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User management operations with role-based authorization")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final AuthorizationService authorizationService;

    public UserManagementController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Get all users - requires USER_READ permission
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (requires USER_READ permission)")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    @PreAuthorize("hasAuthority('PERMISSION_USER_READ')")
    public ResponseEntity<List<User>> getAllUsers() {
        // Implementation would fetch users from service
        // For demonstration, returning empty list
        return ResponseEntity.ok(List.of());
    }

    /**
     * Get user by ID - requires USER_READ permission or user accessing their own data
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
    @PreAuthorize("hasAuthority('PERMISSION_USER_READ') or @authorizationService.canAccessUserResource(#userId)")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        // Implementation would fetch user from service
        return ResponseEntity.ok(new User());
    }

    /**
     * Create new user - requires USER_CREATE permission
     */
    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user account")
    @RequirePermission("USER_CREATE")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Implementation would create user via service
        return ResponseEntity.ok(user);
    }

    /**
     * Update user - requires USER_UPDATE permission or user updating their own data
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @PreAuthorize("hasAuthority('PERMISSION_USER_UPDATE') or @authorizationService.canAccessUserResource(#userId)")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId, 
            @RequestBody User user) {
        // Implementation would update user via service
        return ResponseEntity.ok(user);
    }

    /**
     * Delete user - requires USER_DELETE permission
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    @RequirePermission("USER_DELETE")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        // Implementation would delete user via service
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign role to user - requires USER_MANAGE_ROLES permission
     */
    @PostMapping("/{userId}/roles/{role}")
    @Operation(summary = "Assign role to user", description = "Assign a role to a user")
    @RequirePermission("USER_MANAGE_ROLES")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId, 
            @PathVariable Role role) {
        // Validate that current user can manage the target user
        authorizationService.requirePermission(Permission.USER_MANAGE_ROLES);
        
        // Implementation would assign role via service
        return ResponseEntity.ok().build();
    }

    /**
     * Remove role from user - requires USER_MANAGE_ROLES permission
     */
    @DeleteMapping("/{userId}/roles/{role}")
    @Operation(summary = "Remove role from user", description = "Remove a role from a user")
    @RequirePermission("USER_MANAGE_ROLES")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId, 
            @PathVariable Role role) {
        // Implementation would remove role via service
        return ResponseEntity.ok().build();
    }

    /**
     * Get user permissions - requires USER_READ permission or user accessing their own data
     */
    @GetMapping("/{userId}/permissions")
    @Operation(summary = "Get user permissions", description = "Get all permissions for a user")
    @PreAuthorize("hasAuthority('PERMISSION_USER_READ') or @authorizationService.canAccessUserResource(#userId)")
    public ResponseEntity<Set<Permission>> getUserPermissions(@PathVariable UUID userId) {
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser.getAllPermissions());
        }
        return ResponseEntity.ok(Set.of());
    }

    /**
     * Get current user info - requires authentication
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> userInfo = Map.of(
            "id", currentUser.getId(),
            "username", currentUser.getUsername(),
            "email", currentUser.getEmail(),
            "fullName", currentUser.getFullName(),
            "roles", currentUser.getRoles(),
            "permissions", currentUser.getAllPermissions(),
            "isAdmin", currentUser.isAdmin()
        );

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Admin-only endpoint - requires ADMIN role
     */
    @GetMapping("/admin/stats")
    @Operation(summary = "Get user statistics", description = "Get user statistics (admin only)")
    @RequireAdmin
    public ResponseEntity<Map<String, Object>> getUserStats() {
        // Implementation would get user statistics
        Map<String, Object> stats = Map.of(
            "totalUsers", 0,
            "activeUsers", 0,
            "adminUsers", 0
        );
        return ResponseEntity.ok(stats);
    }

    /**
     * Super admin only endpoint - requires SUPER_ADMIN role
     */
    @PostMapping("/admin/system-user")
    @Operation(summary = "Create system user", description = "Create a system user (super admin only)")
    @RequireRole("SUPER_ADMIN")
    public ResponseEntity<User> createSystemUser(@RequestBody User user) {
        // Implementation would create system user
        return ResponseEntity.ok(user);
    }

    /**
     * Check user permissions - utility endpoint
     */
    @GetMapping("/check-permission/{permission}")
    @Operation(summary = "Check permission", description = "Check if current user has a specific permission")
    public ResponseEntity<Map<String, Boolean>> checkPermission(@PathVariable String permission) {
        try {
            Permission perm = Permission.valueOf(permission.toUpperCase());
            boolean hasPermission = authorizationService.hasPermission(perm);
            return ResponseEntity.ok(Map.of("hasPermission", hasPermission));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check user role - utility endpoint
     */
    @GetMapping("/check-role/{role}")
    @Operation(summary = "Check role", description = "Check if current user has a specific role")
    public ResponseEntity<Map<String, Boolean>> checkRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            boolean hasRole = authorizationService.hasRole(userRole);
            return ResponseEntity.ok(Map.of("hasRole", hasRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}