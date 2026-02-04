package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.Download;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Download entity
 */
@Repository
public interface DownloadRepository extends JpaRepository<Download, UUID> {

    /**
     * Find all public downloads
     */
    List<Download> findByIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc();

    /**
     * Find downloads by category
     */
    Page<Download> findByCategoryAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(
            String category, Pageable pageable);

    /**
     * Find downloads by category (list)
     */
    List<Download> findByCategoryAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(String category);

    /**
     * Search downloads by title
     */
    Page<Download> findByTitleContainingIgnoreCaseAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(
            String title, Pageable pageable);

    /**
     * Get popular downloads
     */
    List<Download> findTop10ByIsPublicTrueAndIsActiveTrueOrderByDownloadCountDesc();

    /**
     * Count downloads by category
     */
    long countByCategoryAndIsPublicTrueAndIsActiveTrue(String category);

    /**
     * Get all categories
     */
    List<String> findDistinctCategoryByIsPublicTrueAndIsActiveTrue();
}
