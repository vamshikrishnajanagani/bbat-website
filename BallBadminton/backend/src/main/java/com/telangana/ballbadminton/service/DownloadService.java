package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.download.DownloadRequest;
import com.telangana.ballbadminton.dto.download.DownloadResponse;
import com.telangana.ballbadminton.entity.Download;
import com.telangana.ballbadminton.repository.DownloadRepository;
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
 * Service class for Download management
 */
@Service
@Transactional
public class DownloadService {

    private final DownloadRepository downloadRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public DownloadService(DownloadRepository downloadRepository, FileUploadService fileUploadService) {
        this.downloadRepository = downloadRepository;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Get all public downloads
     */
    @Transactional(readOnly = true)
    public List<DownloadResponse> getAllPublicDownloads() {
        return downloadRepository.findByIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get downloads by category with pagination
     */
    @Transactional(readOnly = true)
    public Page<DownloadResponse> getDownloadsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return downloadRepository.findByCategoryAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(category, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get downloads by category (list)
     */
    @Transactional(readOnly = true)
    public List<DownloadResponse> getDownloadsByCategoryList(String category) {
        return downloadRepository.findByCategoryAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(category)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search downloads
     */
    @Transactional(readOnly = true)
    public Page<DownloadResponse> searchDownloads(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return downloadRepository.findByTitleContainingIgnoreCaseAndIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc(title, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get popular downloads
     */
    @Transactional(readOnly = true)
    public List<DownloadResponse> getPopularDownloads() {
        return downloadRepository.findTop10ByIsPublicTrueAndIsActiveTrueOrderByDownloadCountDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get download by ID
     */
    @Transactional(readOnly = true)
    public Optional<DownloadResponse> getDownloadById(UUID id) {
        return downloadRepository.findById(id)
                .map(this::convertToResponse);
    }

    /**
     * Create download
     */
    public DownloadResponse createDownload(DownloadRequest request) {
        Download download = convertToEntity(request);
        Download savedDownload = downloadRepository.save(download);
        return convertToResponse(savedDownload);
    }

    /**
     * Upload file and create download
     */
    public DownloadResponse uploadFile(MultipartFile file, String title, String description, String category) {
        // Upload file
        String fileUrl = fileUploadService.uploadFile(file, "downloads");
        
        // Create download
        Download download = new Download();
        download.setTitle(title);
        download.setDescription(description);
        download.setFileUrl(fileUrl);
        download.setFileName(file.getOriginalFilename());
        download.setFileSize(file.getSize());
        download.setMimeType(file.getContentType());
        download.setCategory(category);
        download.setIsPublic(true);
        download.setIsActive(true);

        Download savedDownload = downloadRepository.save(download);
        return convertToResponse(savedDownload);
    }

    /**
     * Update download
     */
    public DownloadResponse updateDownload(UUID id, DownloadRequest request) {
        Download download = downloadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Download not found with id: " + id));

        updateEntityFromRequest(download, request);
        Download savedDownload = downloadRepository.save(download);
        return convertToResponse(savedDownload);
    }

    /**
     * Delete download
     */
    public void deleteDownload(UUID id) {
        Download download = downloadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Download not found with id: " + id));
        
        download.setIsActive(false);
        downloadRepository.save(download);
    }

    /**
     * Track download
     */
    public void trackDownload(UUID id) {
        Download download = downloadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Download not found with id: " + id));
        
        download.incrementDownloadCount();
        downloadRepository.save(download);
    }

    // Helper methods
    private DownloadResponse convertToResponse(Download download) {
        DownloadResponse response = new DownloadResponse();
        response.setId(download.getId());
        response.setTitle(download.getTitle());
        response.setDescription(download.getDescription());
        response.setFileUrl(download.getFileUrl());
        response.setFileName(download.getFileName());
        response.setFileSize(download.getFileSize());
        response.setFormattedFileSize(download.getFormattedFileSize());
        response.setMimeType(download.getMimeType());
        response.setCategory(download.getCategory());
        response.setDownloadCount(download.getDownloadCount());
        response.setIsPublic(download.getIsPublic());
        response.setIsActive(download.getIsActive());
        response.setFileExtension(download.getFileExtension());
        response.setFileTypeIcon(download.getFileTypeIcon());
        response.setCreatedAt(download.getCreatedAt());
        response.setUpdatedAt(download.getUpdatedAt());
        return response;
    }

    private Download convertToEntity(DownloadRequest request) {
        Download download = new Download();
        updateEntityFromRequest(download, request);
        return download;
    }

    private void updateEntityFromRequest(Download download, DownloadRequest request) {
        download.setTitle(request.getTitle());
        download.setDescription(request.getDescription());
        download.setFileUrl(request.getFileUrl());
        download.setFileName(request.getFileName());
        download.setFileSize(request.getFileSize());
        download.setMimeType(request.getMimeType());
        download.setCategory(request.getCategory());
        download.setIsPublic(request.getIsPublic());
        download.setIsActive(request.getIsActive());
    }
}
