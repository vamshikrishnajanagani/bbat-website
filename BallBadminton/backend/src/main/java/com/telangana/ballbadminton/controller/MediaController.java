package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.media.MediaGalleryRequest;
import com.telangana.ballbadminton.dto.media.MediaGalleryResponse;
import com.telangana.ballbadminton.dto.media.MediaItemRequest;
import com.telangana.ballbadminton.dto.media.MediaItemResponse;
import com.telangana.ballbadminton.entity.MediaGallery;
import com.telangana.ballbadminton.entity.MediaItem;
import com.telangana.ballbadminton.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Media management
 */
@RestController
@RequestMapping("/api/v1/media")
@Tag(name = "Media", description = "Media gallery and item management operations")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // Media Gallery endpoints
    @GetMapping("/galleries")
    @Operation(summary = "Get public galleries with pagination")
    public ResponseEntity<Page<MediaGalleryResponse>> getPublicGalleries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MediaGalleryResponse> galleries = mediaService.getPublicGalleries(page, size);
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/galleries/type/{galleryType}")
    @Operation(summary = "Get galleries by type")
    public ResponseEntity<Page<MediaGalleryResponse>> getGalleriesByType(
            @PathVariable MediaGallery.GalleryType galleryType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MediaGalleryResponse> galleries = mediaService.getGalleriesByType(galleryType, page, size);
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/galleries/featured")
    @Operation(summary = "Get featured galleries")
    public ResponseEntity<List<MediaGalleryResponse>> getFeaturedGalleries() {
        List<MediaGalleryResponse> galleries = mediaService.getFeaturedGalleries();
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/galleries/search")
    @Operation(summary = "Search galleries by title")
    public ResponseEntity<Page<MediaGalleryResponse>> searchGalleries(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MediaGalleryResponse> galleries = mediaService.searchGalleries(title, page, size);
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/galleries/{id}")
    @Operation(summary = "Get gallery by ID with media items")
    public ResponseEntity<MediaGalleryResponse> getGalleryById(@PathVariable UUID id) {
        return mediaService.getGalleryById(id)
                .map(gallery -> ResponseEntity.ok(gallery))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/galleries")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_CREATE')")
    @Operation(summary = "Create media gallery")
    public ResponseEntity<MediaGalleryResponse> createGallery(@Valid @RequestBody MediaGalleryRequest request) {
        MediaGalleryResponse gallery = mediaService.createGallery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(gallery);
    }

    @PutMapping("/galleries/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_UPDATE')")
    @Operation(summary = "Update media gallery")
    public ResponseEntity<MediaGalleryResponse> updateGallery(
            @PathVariable UUID id,
            @Valid @RequestBody MediaGalleryRequest request) {
        MediaGalleryResponse gallery = mediaService.updateGallery(id, request);
        return ResponseEntity.ok(gallery);
    }

    @DeleteMapping("/galleries/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_DELETE')")
    @Operation(summary = "Delete media gallery")
    public ResponseEntity<Void> deleteGallery(@PathVariable UUID id) {
        mediaService.deleteGallery(id);
        return ResponseEntity.noContent().build();
    }

    // Media Item endpoints
    @GetMapping("/galleries/{galleryId}/items")
    @Operation(summary = "Get media items by gallery")
    public ResponseEntity<List<MediaItemResponse>> getMediaItemsByGallery(@PathVariable UUID galleryId) {
        List<MediaItemResponse> items = mediaService.getMediaItemsByGallery(galleryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/type/{mediaType}")
    @Operation(summary = "Get media items by type")
    public ResponseEntity<Page<MediaItemResponse>> getMediaItemsByType(
            @PathVariable MediaItem.MediaType mediaType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MediaItemResponse> items = mediaService.getMediaItemsByType(mediaType, page, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "Get media item by ID")
    public ResponseEntity<MediaItemResponse> getMediaItemById(@PathVariable UUID id) {
        return mediaService.getMediaItemById(id)
                .map(item -> ResponseEntity.ok(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_CREATE')")
    @Operation(summary = "Create media item")
    public ResponseEntity<MediaItemResponse> createMediaItem(@Valid @RequestBody MediaItemRequest request) {
        MediaItemResponse item = mediaService.createMediaItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PostMapping("/galleries/{galleryId}/upload")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_CREATE')")
    @Operation(summary = "Upload media file to gallery")
    public ResponseEntity<MediaItemResponse> uploadMediaFile(
            @PathVariable UUID galleryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        MediaItemResponse item = mediaService.uploadMediaFile(galleryId, file, title, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_UPDATE')")
    @Operation(summary = "Update media item")
    public ResponseEntity<MediaItemResponse> updateMediaItem(
            @PathVariable UUID id,
            @Valid @RequestBody MediaItemRequest request) {
        MediaItemResponse item = mediaService.updateMediaItem(id, request);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_DELETE')")
    @Operation(summary = "Delete media item")
    public ResponseEntity<Void> deleteMediaItem(@PathVariable UUID id) {
        mediaService.deleteMediaItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('PERMISSION_MEDIA_READ')")
    @Operation(summary = "Get media statistics")
    public ResponseEntity<Map<String, Object>> getMediaStatistics() {
        MediaService.MediaStatistics stats = mediaService.getMediaStatistics();
        Map<String, Object> response = Map.of(
                "totalGalleries", stats.getTotalGalleries(),
                "photoGalleries", stats.getPhotoGalleries(),
                "videoGalleries", stats.getVideoGalleries(),
                "totalImages", stats.getTotalImages(),
                "totalVideos", stats.getTotalVideos()
        );
        return ResponseEntity.ok(response);
    }
}