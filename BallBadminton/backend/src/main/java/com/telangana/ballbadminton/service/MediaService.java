package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.media.MediaGalleryRequest;
import com.telangana.ballbadminton.dto.media.MediaGalleryResponse;
import com.telangana.ballbadminton.dto.media.MediaItemRequest;
import com.telangana.ballbadminton.dto.media.MediaItemResponse;
import com.telangana.ballbadminton.entity.MediaGallery;
import com.telangana.ballbadminton.entity.MediaItem;
import com.telangana.ballbadminton.repository.MediaGalleryRepository;
import com.telangana.ballbadminton.repository.MediaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for Media management
 */
@Service
@Transactional
public class MediaService {

    private final MediaGalleryRepository mediaGalleryRepository;
    private final MediaItemRepository mediaItemRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public MediaService(MediaGalleryRepository mediaGalleryRepository,
                       MediaItemRepository mediaItemRepository,
                       FileUploadService fileUploadService) {
        this.mediaGalleryRepository = mediaGalleryRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.fileUploadService = fileUploadService;
    }

    // Media Gallery methods
    /**
     * Get public galleries with pagination
     */
    @Transactional(readOnly = true)
    public Page<MediaGalleryResponse> getPublicGalleries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mediaGalleryRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
                .map(this::convertGalleryToResponse);
    }

    /**
     * Get galleries by type
     */
    @Transactional(readOnly = true)
    public Page<MediaGalleryResponse> getGalleriesByType(MediaGallery.GalleryType galleryType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mediaGalleryRepository.findByGalleryTypeAndIsPublicTrueOrderByCreatedAtDesc(galleryType, pageable)
                .map(this::convertGalleryToResponse);
    }

    /**
     * Get featured galleries
     */
    @Transactional(readOnly = true)
    public List<MediaGalleryResponse> getFeaturedGalleries() {
        return mediaGalleryRepository.findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::convertGalleryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search galleries by title
     */
    @Transactional(readOnly = true)
    public Page<MediaGalleryResponse> searchGalleries(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mediaGalleryRepository.findByTitleContainingIgnoreCaseAndIsPublicTrueOrderByCreatedAtDesc(title, pageable)
                .map(this::convertGalleryToResponse);
    }

    /**
     * Get gallery by ID
     */
    @Transactional(readOnly = true)
    public Optional<MediaGalleryResponse> getGalleryById(UUID id) {
        return mediaGalleryRepository.findById(id)
                .map(this::convertGalleryToResponseWithItems);
    }

    /**
     * Create media gallery
     */
    public MediaGalleryResponse createGallery(MediaGalleryRequest request) {
        MediaGallery gallery = convertGalleryToEntity(request);
        MediaGallery savedGallery = mediaGalleryRepository.save(gallery);
        return convertGalleryToResponse(savedGallery);
    }

    /**
     * Update media gallery
     */
    public MediaGalleryResponse updateGallery(UUID id, MediaGalleryRequest request) {
        MediaGallery gallery = mediaGalleryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found with id: " + id));

        updateGalleryFromRequest(gallery, request);
        MediaGallery savedGallery = mediaGalleryRepository.save(gallery);
        return convertGalleryToResponse(savedGallery);
    }

    /**
     * Delete media gallery
     */
    public void deleteGallery(UUID id) {
        if (!mediaGalleryRepository.existsById(id)) {
            throw new IllegalArgumentException("Gallery not found with id: " + id);
        }
        mediaGalleryRepository.deleteById(id);
    }

    // Media Item methods
    /**
     * Get media items by gallery
     */
    @Transactional(readOnly = true)
    public List<MediaItemResponse> getMediaItemsByGallery(UUID galleryId) {
        MediaGallery gallery = mediaGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found with id: " + galleryId));
        
        return mediaItemRepository.findByGalleryAndIsActiveTrueOrderBySortOrder(gallery)
                .stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get media items by type
     */
    @Transactional(readOnly = true)
    public Page<MediaItemResponse> getMediaItemsByType(MediaItem.MediaType mediaType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mediaItemRepository.findByMediaTypeAndIsActiveTrueOrderBySortOrder(mediaType, pageable)
                .map(this::convertItemToResponse);
    }

    /**
     * Get media item by ID
     */
    @Transactional(readOnly = true)
    public Optional<MediaItemResponse> getMediaItemById(UUID id) {
        return mediaItemRepository.findById(id)
                .map(this::convertItemToResponse);
    }

    /**
     * Create media item
     */
    public MediaItemResponse createMediaItem(MediaItemRequest request) {
        MediaGallery gallery = mediaGalleryRepository.findById(request.getGalleryId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found with id: " + request.getGalleryId()));

        MediaItem mediaItem = convertItemToEntity(request);
        mediaItem.setGallery(gallery);
        MediaItem savedItem = mediaItemRepository.save(mediaItem);
        return convertItemToResponse(savedItem);
    }

    /**
     * Upload media file and create media item
     */
    public MediaItemResponse uploadMediaFile(UUID galleryId, MultipartFile file, String title, String description) {
        MediaGallery gallery = mediaGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found with id: " + galleryId));

        // Upload file
        String fileUrl = fileUploadService.uploadMediaFile(file);
        
        // Determine media type from file
        MediaItem.MediaType mediaType = determineMediaType(file.getContentType());
        
        // Create media item
        MediaItem mediaItem = new MediaItem();
        mediaItem.setGallery(gallery);
        mediaItem.setTitle(title);
        mediaItem.setDescription(description);
        mediaItem.setFileUrl(fileUrl);
        mediaItem.setMediaType(mediaType);
        mediaItem.setFileSize(file.getSize());
        mediaItem.setMimeType(file.getContentType());
        mediaItem.setSortOrder(0);
        mediaItem.setIsActive(true);

        MediaItem savedItem = mediaItemRepository.save(mediaItem);
        return convertItemToResponse(savedItem);
    }

    /**
     * Update media item
     */
    public MediaItemResponse updateMediaItem(UUID id, MediaItemRequest request) {
        MediaItem mediaItem = mediaItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Media item not found with id: " + id));

        updateItemFromRequest(mediaItem, request);
        MediaItem savedItem = mediaItemRepository.save(mediaItem);
        return convertItemToResponse(savedItem);
    }

    /**
     * Delete media item
     */
    public void deleteMediaItem(UUID id) {
        MediaItem mediaItem = mediaItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Media item not found with id: " + id));
        
        mediaItem.setIsActive(false);
        mediaItemRepository.save(mediaItem);
    }

    /**
     * Get media statistics
     */
    @Transactional(readOnly = true)
    public MediaStatistics getMediaStatistics() {
        long totalGalleries = mediaGalleryRepository.countByIsPublicTrue();
        long photoGalleries = mediaGalleryRepository.countByGalleryTypeAndIsPublicTrue(MediaGallery.GalleryType.PHOTO);
        long videoGalleries = mediaGalleryRepository.countByGalleryTypeAndIsPublicTrue(MediaGallery.GalleryType.VIDEO);
        long totalImages = mediaItemRepository.countByMediaTypeAndIsActiveTrue(MediaItem.MediaType.IMAGE);
        long totalVideos = mediaItemRepository.countByMediaTypeAndIsActiveTrue(MediaItem.MediaType.VIDEO);

        return new MediaStatistics(totalGalleries, photoGalleries, videoGalleries, totalImages, totalVideos);
    }

    // Helper methods for MediaGallery
    private MediaGalleryResponse convertGalleryToResponse(MediaGallery gallery) {
        MediaGalleryResponse response = new MediaGalleryResponse();
        response.setId(gallery.getId());
        response.setTitle(gallery.getTitle());
        response.setDescription(gallery.getDescription());
        response.setGalleryType(gallery.getGalleryType());
        response.setCoverImageUrl(gallery.getCoverImageUrl());
        response.setIsFeatured(gallery.getIsFeatured());
        response.setIsPublic(gallery.getIsPublic());
        response.setMediaItemCount(gallery.getActiveMediaItemCount());
        response.setCreatedAt(gallery.getCreatedAt());
        response.setUpdatedAt(gallery.getUpdatedAt());
        return response;
    }

    private MediaGalleryResponse convertGalleryToResponseWithItems(MediaGallery gallery) {
        MediaGalleryResponse response = convertGalleryToResponse(gallery);
        List<MediaItemResponse> items = gallery.getActiveMediaItems()
                .stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());
        response.setMediaItems(items);
        return response;
    }

    private MediaGallery convertGalleryToEntity(MediaGalleryRequest request) {
        MediaGallery gallery = new MediaGallery();
        updateGalleryFromRequest(gallery, request);
        return gallery;
    }

    private void updateGalleryFromRequest(MediaGallery gallery, MediaGalleryRequest request) {
        gallery.setTitle(request.getTitle());
        gallery.setDescription(request.getDescription());
        gallery.setGalleryType(request.getGalleryType());
        gallery.setCoverImageUrl(request.getCoverImageUrl());
        gallery.setIsFeatured(request.getIsFeatured());
        gallery.setIsPublic(request.getIsPublic());
    }

    // Helper methods for MediaItem
    private MediaItemResponse convertItemToResponse(MediaItem item) {
        MediaItemResponse response = new MediaItemResponse();
        response.setId(item.getId());
        response.setGalleryId(item.getGallery().getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setFileUrl(item.getFileUrl());
        response.setThumbnailUrl(item.getThumbnailUrl());
        response.setMediaType(item.getMediaType());
        response.setFileSize(item.getFileSize());
        response.setMimeType(item.getMimeType());
        response.setSortOrder(item.getSortOrder());
        response.setIsActive(item.getIsActive());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    private MediaItem convertItemToEntity(MediaItemRequest request) {
        MediaItem item = new MediaItem();
        updateItemFromRequest(item, request);
        return item;
    }

    private void updateItemFromRequest(MediaItem item, MediaItemRequest request) {
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setFileUrl(request.getFileUrl());
        item.setThumbnailUrl(request.getThumbnailUrl());
        item.setMediaType(request.getMediaType());
        item.setFileSize(request.getFileSize());
        item.setMimeType(request.getMimeType());
        item.setSortOrder(request.getSortOrder());
        item.setIsActive(request.getIsActive());
    }

    private MediaItem.MediaType determineMediaType(String mimeType) {
        if (mimeType == null) {
            return MediaItem.MediaType.IMAGE;
        }
        
        if (mimeType.startsWith("image/")) {
            return MediaItem.MediaType.IMAGE;
        } else if (mimeType.startsWith("video/")) {
            return MediaItem.MediaType.VIDEO;
        } else if (mimeType.startsWith("audio/")) {
            return MediaItem.MediaType.AUDIO;
        }
        
        return MediaItem.MediaType.IMAGE;
    }

    // Statistics class
    public static class MediaStatistics {
        private final long totalGalleries;
        private final long photoGalleries;
        private final long videoGalleries;
        private final long totalImages;
        private final long totalVideos;

        public MediaStatistics(long totalGalleries, long photoGalleries, long videoGalleries, 
                              long totalImages, long totalVideos) {
            this.totalGalleries = totalGalleries;
            this.photoGalleries = photoGalleries;
            this.videoGalleries = videoGalleries;
            this.totalImages = totalImages;
            this.totalVideos = totalVideos;
        }

        public long getTotalGalleries() { return totalGalleries; }
        public long getPhotoGalleries() { return photoGalleries; }
        public long getVideoGalleries() { return videoGalleries; }
        public long getTotalImages() { return totalImages; }
        public long getTotalVideos() { return totalVideos; }
    }
}