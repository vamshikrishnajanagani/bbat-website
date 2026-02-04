package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Tournament entity
 * Basic repository for tournament data access
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    // Basic CRUD operations are inherited from JpaRepository
    // Additional tournament-specific methods can be added here as needed
}