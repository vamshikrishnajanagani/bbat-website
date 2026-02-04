package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.entity.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for NewsArticle entity
 * Provides data access methods for news article management
 */
@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID> {

    /**
     * Find all published articles ordered by published date
     */
    Page<NewsArticle> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    /**
     * Find published articles by category
     */
    Page<NewsArticle> findByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(NewsCategory category, Pageable pageable);

    /**
     * Find featured articles
     */
    List<NewsArticle> findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc();

    /**
     * Find article by slug
     */
    Optional<NewsArticle> findBySlugAndIsPublishedTrue(String slug);

    /**
     * Search articles by title or content
     */
    @Query("SELECT a FROM NewsArticle a WHERE a.isPublished = true AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY a.publishedAt DESC")
    Page<NewsArticle> searchPublishedArticles(@Param("query") String query, Pageable pageable);

    /**
     * Find articles published within date range
     */
    Page<NewsArticle> findByIsPublishedTrueAndPublishedAtBetweenOrderByPublishedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find articles by language
     */
    Page<NewsArticle> findByLanguageAndIsPublishedTrueOrderByPublishedAtDesc(
            NewsArticle.Language language, Pageable pageable);

    /**
     * Find recent articles (published in last N days)
     */
    @Query("SELECT a FROM NewsArticle a WHERE a.isPublished = true AND " +
           "a.publishedAt >= :sinceDate ORDER BY a.publishedAt DESC")
    List<NewsArticle> findRecentArticles(@Param("sinceDate") LocalDateTime sinceDate);

    /**
     * Count published articles
     */
    long countByIsPublishedTrue();

    /**
     * Count articles by category
     */
    long countByCategoryAndIsPublishedTrue(NewsCategory category);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Find all articles (including unpublished) for admin
     */
    Page<NewsArticle> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find articles with scheduled publication date that are not yet published
     */
    List<NewsArticle> findByScheduledPublicationDateIsNotNullAndIsPublishedFalse();

    /**
     * Find articles scheduled for publication before a specific date/time
     */
    List<NewsArticle> findByScheduledPublicationDateBeforeAndIsPublishedFalse(LocalDateTime dateTime);

    /**
     * Find articles scheduled for publication within a date range
     */
    List<NewsArticle> findByScheduledPublicationDateBetweenAndIsPublishedFalse(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count articles with scheduled publication date that are not yet published
     */
    long countByScheduledPublicationDateIsNotNullAndIsPublishedFalse();
}