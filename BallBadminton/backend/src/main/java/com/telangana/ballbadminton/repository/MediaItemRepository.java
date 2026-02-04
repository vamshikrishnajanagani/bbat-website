package com.telangana.ballbadminton.repository;

import com.telangana.ballbadminton.entity.MediaGallery;
import com.telangana.ballbadminton.entity.MediaItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for MediaItem entity
 * Provides data access methods for media item management
 */
@Repository
public interface MediaItemRepository extends JpaRepository<MediaItem, UUID> {

    /**
     * Find active media items by gallery
     */
    List<MediaItem> findByGalleryAndIsActiveTrueOrderBySortOrder(MediaGallery gallery);

    /**
     * Find media items by type
     */
    Page<MediaItem> findByMediaTypeAndIsActiveTrueOrderBySortOrder(
            MediaItem.MediaType mediaType, Pageable pageable);

    /**
     * Find media items by gallery and type
     */
    List<MediaItem> findByGalleryAndMediaTypeAndIsActiveTrueOrderBySortOrder(
            MediaGallery gallery, MediaItem.MediaType mediaType);

    /**
     * Count active media items by gallery
     */
    long countByGalleryAndIsActiveTrue(MediaGallery gallery);

    /**
     * Count media items by type
     */
    long countByMediaTypeAndIsActiveTrue(MediaItem.MediaType mediaType);
}