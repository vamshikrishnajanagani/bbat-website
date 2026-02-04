package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.entity.User;
import com.telangana.ballbadminton.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService implementation for Spring Security
 * Loads user details from the database for authentication
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username for Spring Security authentication
     * Supports both username and email as login identifiers
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    logger.warn("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });

        if (!user.getActive()) {
            logger.warn("Inactive user attempted to login: {}", usernameOrEmail);
            throw new UsernameNotFoundException("User account is inactive: " + usernameOrEmail);
        }

        logger.debug("Successfully loaded user: {} with roles: {}", user.getUsername(), user.getRoles());
        return user;
    }

    /**
     * Load user by ID for token validation
     */
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        logger.debug("Loading user by ID: {}", userId);

        try {
            java.util.UUID userUuid = java.util.UUID.fromString(userId);
            User user = userRepository.findById(userUuid)
                    .orElseThrow(() -> {
                        logger.warn("User not found with ID: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });

            if (!user.getActive()) {
                logger.warn("Inactive user found with ID: {}", userId);
                throw new UsernameNotFoundException("User account is inactive: " + userId);
            }

            logger.debug("Successfully loaded user by ID: {}", user.getUsername());
            return user;
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid user ID format: {}", userId);
            throw new UsernameNotFoundException("Invalid user ID format: " + userId);
        }
    }
}