package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations
 * Provides database access methods for user management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email (for login)
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find active users only
     */
    @Query("SELECT u FROM User u WHERE u.active = true")
    java.util.List<User> findAllActiveUsers();

    /**
     * Find user by username and active status
     */
    Optional<User> findByUsernameAndActive(String username, Boolean active);

    /**
     * Find user by email and active status
     */
    Optional<User> findByEmailAndActive(String email, Boolean active);

    /**
     * Update user's last login time
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * Update user's password and password changed timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.passwordChangedAt = :passwordChangedAt WHERE u.id = :userId")
    void updatePassword(@Param("userId") UUID userId, @Param("password") String password, @Param("passwordChangedAt") LocalDateTime passwordChangedAt);

    /**
     * Activate or deactivate user account
     */
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :userId")
    void updateActiveStatus(@Param("userId") UUID userId, @Param("active") Boolean active);

    /**
     * Update email verification status
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = :verified WHERE u.id = :userId")
    void updateEmailVerificationStatus(@Param("userId") UUID userId, @Param("verified") Boolean verified);

    /**
     * Lock or unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = :nonLocked WHERE u.id = :userId")
    void updateAccountLockStatus(@Param("userId") UUID userId, @Param("nonLocked") Boolean nonLocked);

    /**
     * Find users by role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.active = true")
    java.util.List<User> findByRole(@Param("role") com.telangana.ballbadminton.entity.Role role);

    /**
     * Count active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();

    /**
     * Find users created after a specific date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date AND u.active = true")
    java.util.List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Find users who haven't logged in for a specific period
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date OR u.lastLogin IS NULL")
    java.util.List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);
}