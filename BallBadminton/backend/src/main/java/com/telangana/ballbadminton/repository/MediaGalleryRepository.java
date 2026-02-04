package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.MediaGallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for MediaGallery entity
 * Provides data access methods for media gallery management
 */
@Repository
public interface MediaGalleryRepository extends JpaRepository<MediaGallery, UUID> {

    /**
     * Find all public galleries
     */
    Page<MediaGallery> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find featured galleries
     */
    List<MediaGallery> findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc();

    /**
     * Find galleries by type
     */
    Page<MediaGallery> findByGalleryTypeAndIsPublicTrueOrderByCreatedAtDesc(
            MediaGallery.GalleryType galleryType, Pageable pageable);

    /**
     * Search galleries by title
     */
    Page<MediaGallery> findByTitleContainingIgnoreCaseAndIsPublicTrueOrderByCreatedAtDesc(
            String title, Pageable pageable);

    /**
     * Get galleries with media items
     */
    @Query("SELECT g FROM MediaGallery g LEFT JOIN FETCH g.mediaItems WHERE g.isPublic = true ORDER BY g.createdAt DESC")
    List<MediaGallery> findAllWithMediaItems();

    /**
     * Count public galleries
     */
    long countByIsPublicTrue();

    /**
     * Count galleries by type
     */
    long countByGalleryTypeAndIsPublicTrue(MediaGallery.GalleryType galleryType);
}