package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for District entity
 * Provides data access methods for district management
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {

    /**
     * Find all active districts
     */
    List<District> findByIsActiveTrueOrderByName();

    /**
     * Find district by code
     */
    Optional<District> findByCodeAndIsActiveTrue(String code);

    /**
     * Find districts by name containing (case insensitive)
     */
    Page<District> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    /**
     * Get district statistics with player and tournament counts
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.players LEFT JOIN FETCH d.tournaments WHERE d.isActive = true")
    List<District> findAllWithStatistics();

    /**
     * Find districts with player count greater than specified value
     */
    @Query("SELECT d FROM District d WHERE d.isActive = true AND SIZE(d.players) > :minPlayerCount")
    List<District> findDistrictsWithMinimumPlayers(@Param("minPlayerCount") int minPlayerCount);

    /**
     * Get district by name (case insensitive)
     */
    Optional<District> findByNameIgnoreCaseAndIsActiveTrue(String name);

    /**
     * Check if district code exists
     */
    boolean existsByCodeAndIsActiveTrue(String code);

    /**
     * Count active districts
     */
    long countByIsActiveTrue();
}