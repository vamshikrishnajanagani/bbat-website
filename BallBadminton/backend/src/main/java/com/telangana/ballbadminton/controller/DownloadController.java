package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.download.DownloadRequest;
import com.telangana.ballbadminton.dto.download.DownloadResponse;
import com.telangana.ballbadminton.service.DownloadService;
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
import java.util.UUID;

/**
 * REST Controller for Download management
 */
@RestController
@RequestMapping("/api/v1/downloads")
@Tag(name = "Downloads", description = "Download resource management operations")
public class DownloadController {

    private final DownloadService downloadService;

    @Autowired
    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping
    @Operation(summary = "Get all public downloads")
    public ResponseEntity<List<DownloadResponse>> getAllPublicDownloads() {
        List<DownloadResponse> downloads = downloadService.getAllPublicDownloads();
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get downloads by category with pagination")
    public ResponseEntity<Page<DownloadResponse>> getDownloadsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DownloadResponse> downloads = downloadService.getDownloadsByCategory(category, page, size);
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/category/{category}/list")
    @Operation(summary = "Get downloads by category as list")
    public ResponseEntity<List<DownloadResponse>> getDownloadsByCategoryList(@PathVariable String category) {
        List<DownloadResponse> downloads = downloadService.getDownloadsByCategoryList(category);
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/search")
    @Operation(summary = "Search downloads by title")
    public ResponseEntity<Page<DownloadResponse>> searchDownloads(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DownloadResponse> downloads = downloadService.searchDownloads(title, page, size);
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular downloads")
    public ResponseEntity<List<DownloadResponse>> getPopularDownloads() {
        List<DownloadResponse> downloads = downloadService.getPopularDownloads();
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get download by ID")
    public ResponseEntity<DownloadResponse> getDownloadById(@PathVariable UUID id) {
        return downloadService.getDownloadById(id)
                .map(download -> ResponseEntity.ok(download))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_DOWNLOAD_CREATE')")
    @Operation(summary = "Create download")
    public ResponseEntity<DownloadResponse> createDownload(@Valid @RequestBody DownloadRequest request) {
        DownloadResponse download = downloadService.createDownload(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(download);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('PERMISSION_DOWNLOAD_CREATE')")
    @Operation(summary = "Upload file and create download")
    public ResponseEntity<DownloadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category) {
        DownloadResponse download = downloadService.uploadFile(file, title, description, category);
        return ResponseEntity.status(HttpStatus.CREATED).body(download);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DOWNLOAD_UPDATE')")
    @Operation(summary = "Update download")
    public ResponseEntity<DownloadResponse> updateDownload(
            @PathVariable UUID id,
            @Valid @RequestBody DownloadRequest request) {
        DownloadResponse download = downloadService.updateDownload(id, request);
        return ResponseEntity.ok(download);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DOWNLOAD_DELETE')")
    @Operation(summary = "Delete download")
    public ResponseEntity<Void> deleteDownload(@PathVariable UUID id) {
        downloadService.deleteDownload(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/track")
    @Operation(summary = "Track download")
    public ResponseEntity<Void> trackDownload(@PathVariable UUID id) {
        downloadService.trackDownload(id);
        return ResponseEntity.ok().build();
    }
}
