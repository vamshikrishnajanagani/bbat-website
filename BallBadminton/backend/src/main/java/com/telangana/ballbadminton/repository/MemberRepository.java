package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Member entity operations
 * Provides database access methods for member management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    /**
     * Find all active members
     */
    List<Member> findByIsActiveTrue();

    /**
     * Find all active members with pagination
     */
    Page<Member> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all prominent members
     */
    List<Member> findByIsProminentTrue();

    /**
     * Find active and prominent members
     */
    List<Member> findByIsActiveTrueAndIsProminentTrue();

    /**
     * Find members by position
     */
    List<Member> findByPositionContainingIgnoreCase(String position);

    /**
     * Find members by name (case insensitive)
     */
    List<Member> findByNameContainingIgnoreCase(String name);

    /**
     * Find member by email
     */
    Optional<Member> findByEmail(String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find members ordered by hierarchy level
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true ORDER BY m.hierarchyLevel ASC, m.name ASC")
    List<Member> findActiveOrderedByHierarchy();

    /**
     * Find members ordered by hierarchy level with pagination
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true ORDER BY m.hierarchyLevel ASC, m.name ASC")
    Page<Member> findActiveOrderedByHierarchy(Pageable pageable);

    /**
     * Find currently serving members
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND " +
           "(m.tenureStartDate IS NULL OR m.tenureStartDate <= :currentDate) AND " +
           "(m.tenureEndDate IS NULL OR m.tenureEndDate >= :currentDate)")
    List<Member> findCurrentlyServing(@Param("currentDate") LocalDate currentDate);

    /**
     * Find members with expired tenure
     */
    @Query("SELECT m FROM Member m WHERE m.tenureEndDate IS NOT NULL AND m.tenureEndDate < :currentDate")
    List<Member> findWithExpiredTenure(@Param("currentDate") LocalDate currentDate);

    /**
     * Find members by hierarchy level
     */
    List<Member> findByHierarchyLevelAndIsActiveTrue(Integer hierarchyLevel);

    /**
     * Find members with tenure ending soon
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND " +
           "m.tenureEndDate IS NOT NULL AND " +
           "m.tenureEndDate BETWEEN :startDate AND :endDate")
    List<Member> findWithTenureEndingSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Search members by name, position, or email
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.position) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Member> searchMembers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count active members
     */
    long countByIsActiveTrue();

    /**
     * Count prominent members
     */
    long countByIsActiveTrueAndIsProminentTrue();

    /**
     * Count currently serving members
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.isActive = true AND " +
           "(m.tenureStartDate IS NULL OR m.tenureStartDate <= :currentDate) AND " +
           "(m.tenureEndDate IS NULL OR m.tenureEndDate >= :currentDate)")
    long countCurrentlyServing(@Param("currentDate") LocalDate currentDate);

    /**
     * Find members by hierarchy level range
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND " +
           "m.hierarchyLevel BETWEEN :minLevel AND :maxLevel " +
           "ORDER BY m.hierarchyLevel ASC, m.name ASC")
    List<Member> findByHierarchyLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);

    /**
     * Find top-level members (hierarchy level 1-3)
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND " +
           "m.hierarchyLevel BETWEEN 1 AND 3 " +
           "ORDER BY m.hierarchyLevel ASC, m.name ASC")
    List<Member> findTopLevelMembers();
}