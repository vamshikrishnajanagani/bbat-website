package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for TournamentRegistration entity
 * Basic repository for tournament registration data access
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, UUID> {
    // Basic CRUD operations are inherited from JpaRepository
    // Additional registration-specific methods can be added here as needed
}
