package com.telangana.ballbadminton.security;

import com.telangana.ballbadminton.entity.Permission;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling authorization logic and permission checks
 * Provides centralized authorization methods for the application
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class AuthorizationService {

    /**
     * Get the currently authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Check if the current user has a specific permission
     */
    public boolean hasPermission(Permission permission) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasPermission(permission);
    }

    /**
     * Check if the current user has any of the specified permissions
     */
    public boolean hasAnyPermission(Permission... permissions) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasAnyPermission(permissions);
    }

    /**
     * Check if the current user has all of the specified permissions
     */
    public boolean hasAllPermissions(Permission... permissions) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasAllPermissions(permissions);
    }

    /**
     * Check if the current user has a specific role
     */
    public boolean hasRole(Role role) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasRole(role);
    }

    /**
     * Check if the current user has any of the specified roles
     */
    public boolean hasAnyRole(Role... roles) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        for (Role role : roles) {
            if (currentUser.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current user is an administrator
     */
    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Check if the current user can manage another user
     */
    public boolean canManageUser(User targetUser) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.canManageUser(targetUser);
    }

    /**
     * Check if the current user can manage a user with the specified ID
     */
    public boolean canManageUser(UUID userId) {
        // This would typically involve fetching the user from the database
        // For now, we'll implement basic role-based checks
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Super admin can manage anyone
        if (currentUser.hasRole(Role.SUPER_ADMIN)) {
            return true;
        }

        // Admin can manage users (except other super admins)
        if (currentUser.hasRole(Role.ADMIN)) {
            return true; // Additional checks would be needed with user lookup
        }

        // Users can only manage themselves
        return currentUser.getId().equals(userId);
    }

    /**
     * Check if the current user can access a resource owned by another user
     */
    public boolean canAccessUserResource(UUID resourceOwnerId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Admins can access any resource
        if (currentUser.isAdmin()) {
            return true;
        }

        // Users can access their own resources
        return currentUser.getId().equals(resourceOwnerId);
    }

    /**
     * Check if the current user can perform a specific action on a resource type
     */
    public boolean canPerformAction(String resourceType, String action) {
        return switch (resourceType.toLowerCase()) {
            case "member" -> canPerformMemberAction(action);
            case "player" -> canPerformPlayerAction(action);
            case "tournament" -> canPerformTournamentAction(action);
            case "news" -> canPerformNewsAction(action);
            case "media" -> canPerformMediaAction(action);
            case "district" -> canPerformDistrictAction(action);
            case "user" -> canPerformUserAction(action);
            default -> false;
        };
    }

    private boolean canPerformMemberAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.MEMBER_CREATE);
            case "read" -> hasPermission(Permission.MEMBER_READ);
            case "update" -> hasPermission(Permission.MEMBER_UPDATE);
            case "delete" -> hasPermission(Permission.MEMBER_DELETE);
            case "manage_hierarchy" -> hasPermission(Permission.MEMBER_MANAGE_HIERARCHY);
            default -> false;
        };
    }

    private boolean canPerformPlayerAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.PLAYER_CREATE);
            case "read" -> hasPermission(Permission.PLAYER_READ);
            case "update" -> hasPermission(Permission.PLAYER_UPDATE);
            case "delete" -> hasPermission(Permission.PLAYER_DELETE);
            case "manage_statistics" -> hasPermission(Permission.PLAYER_MANAGE_STATISTICS);
            case "manage_achievements" -> hasPermission(Permission.PLAYER_MANAGE_ACHIEVEMENTS);
            default -> false;
        };
    }

    private boolean canPerformTournamentAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.TOURNAMENT_CREATE);
            case "read" -> hasPermission(Permission.TOURNAMENT_READ);
            case "update" -> hasPermission(Permission.TOURNAMENT_UPDATE);
            case "delete" -> hasPermission(Permission.TOURNAMENT_DELETE);
            case "manage_registration" -> hasPermission(Permission.TOURNAMENT_MANAGE_REGISTRATION);
            case "manage_results" -> hasPermission(Permission.TOURNAMENT_MANAGE_RESULTS);
            default -> false;
        };
    }

    private boolean canPerformNewsAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.NEWS_CREATE);
            case "read" -> hasPermission(Permission.NEWS_READ);
            case "update" -> hasPermission(Permission.NEWS_UPDATE);
            case "delete" -> hasPermission(Permission.NEWS_DELETE);
            case "publish" -> hasPermission(Permission.NEWS_PUBLISH);
            case "moderate" -> hasPermission(Permission.NEWS_MODERATE);
            default -> false;
        };
    }

    private boolean canPerformMediaAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.MEDIA_CREATE);
            case "read" -> hasPermission(Permission.MEDIA_READ);
            case "update" -> hasPermission(Permission.MEDIA_UPDATE);
            case "delete" -> hasPermission(Permission.MEDIA_DELETE);
            case "manage_galleries" -> hasPermission(Permission.MEDIA_MANAGE_GALLERIES);
            default -> false;
        };
    }

    private boolean canPerformDistrictAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.DISTRICT_CREATE);
            case "read" -> hasPermission(Permission.DISTRICT_READ);
            case "update" -> hasPermission(Permission.DISTRICT_UPDATE);
            case "delete" -> hasPermission(Permission.DISTRICT_DELETE);
            case "manage_statistics" -> hasPermission(Permission.DISTRICT_MANAGE_STATISTICS);
            default -> false;
        };
    }

    private boolean canPerformUserAction(String action) {
        return switch (action.toLowerCase()) {
            case "create" -> hasPermission(Permission.USER_CREATE);
            case "read" -> hasPermission(Permission.USER_READ);
            case "update" -> hasPermission(Permission.USER_UPDATE);
            case "delete" -> hasPermission(Permission.USER_DELETE);
            case "manage_roles" -> hasPermission(Permission.USER_MANAGE_ROLES);
            default -> false;
        };
    }

    /**
     * Validate that the current user can perform an action, throwing an exception if not
     */
    public void requirePermission(Permission permission) {
        if (!hasPermission(permission)) {
            throw new SecurityException("Access denied: Missing required permission " + permission.getDisplayName());
        }
    }

    /**
     * Validate that the current user has any of the specified permissions
     */
    public void requireAnyPermission(Permission... permissions) {
        if (!hasAnyPermission(permissions)) {
            throw new SecurityException("Access denied: Missing required permissions");
        }
    }

    /**
     * Validate that the current user has a specific role
     */
    public void requireRole(Role role) {
        if (!hasRole(role)) {
            throw new SecurityException("Access denied: Missing required role " + role.getDisplayName());
        }
    }

    /**
     * Validate that the current user is an administrator
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new SecurityException("Access denied: Administrative privileges required");
        }
    }
}