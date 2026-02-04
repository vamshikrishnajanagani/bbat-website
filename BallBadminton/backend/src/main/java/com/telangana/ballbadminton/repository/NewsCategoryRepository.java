package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for NewsCategory entity
 * Provides data access methods for news category management
 */
@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, UUID> {

    /**
     * Find all active categories
     */
    List<NewsCategory> findByIsActiveTrueOrderByName();

    /**
     * Find category by slug
     */
    Optional<NewsCategory> findBySlugAndIsActiveTrue(String slug);

    /**
     * Find category by name (case insensitive)
     */
    Optional<NewsCategory> findByNameIgnoreCaseAndIsActiveTrue(String name);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Get categories with article counts
     */
    @Query("SELECT c FROM NewsCategory c LEFT JOIN FETCH c.articles WHERE c.isActive = true ORDER BY c.name")
    List<NewsCategory> findAllWithArticles();

    /**
     * Count active categories
     */
    long countByIsActiveTrue();
}