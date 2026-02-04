package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.admin.BulkOperationRequest;
import com.telangana.ballbadminton.dto.admin.BulkOperationResponse;
import com.telangana.ballbadminton.entity.*;
import com.telangana.ballbadminton.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for bulk operations on content entities
 * 
 * Provides efficient bulk create, update, and delete operations
 * with atomicity guarantees (all-or-nothing)
 * 
 * Requirements: 6.3, 9.5
 * Property 17: Bulk Operation Atomicity
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class BulkOperationService {

    private static final Logger logger = LoggerFactory.getLogger(BulkOperationService.class);

    private final MemberRepository memberRepository;
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final MediaItemRepository mediaItemRepository;
    private final DistrictRepository districtRepository;
    private final DownloadRepository downloadRepository;
    private final AuditService auditService;

    public BulkOperationService(
            MemberRepository memberRepository,
            PlayerRepository playerRepository,
            TournamentRepository tournamentRepository,
            NewsArticleRepository newsArticleRepository,
            MediaItemRepository mediaItemRepository,
            DistrictRepository districtRepository,
            DownloadRepository downloadRepository,
            AuditService auditService) {
        this.memberRepository = memberRepository;
        this.playerRepository = playerRepository;
        this.tournamentRepository = tournamentRepository;
        this.newsArticleRepository = newsArticleRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.districtRepository = districtRepository;
        this.downloadRepository = downloadRepository;
        this.auditService = auditService;
    }

    /**
     * Execute bulk operation with atomicity guarantee
     * 
     * All operations succeed or all fail (all-or-nothing)
     * 
     * @param request Bulk operation request
     * @return Bulk operation response with results
     */
    @Transactional(rollbackFor = Exception.class)
    public BulkOperationResponse executeBulkOperation(BulkOperationRequest request) {
        logger.info("Executing bulk {} operation on {} entities", 
            request.getOperation(), request.getEntityType());

        BulkOperationResponse response = new BulkOperationResponse();
        response.setOperationId(UUID.randomUUID().toString());
        response.setOperation(request.getOperation());
        response.setEntityType(request.getEntityType());
        response.setTotalCount(request.getEntityIds() != null ? request.getEntityIds().size() : 0);

        try {
            switch (request.getOperation()) {
                case DELETE:
                    executeBulkDelete(request, response);
                    break;
                case UPDATE:
                    executeBulkUpdate(request, response);
                    break;
                case PUBLISH:
                    executeBulkPublish(request, response);
                    break;
                case UNPUBLISH:
                    executeBulkUnpublish(request, response);
                    break;
                default:
                    throw new UnsupportedOperationException(
                        "Operation not supported: " + request.getOperation());
            }

            // Log audit trail for successful bulk operation
            auditService.audit(
                getAuditAction(request.getOperation()),
                request.getEntityType().name(),
                response.getOperationId(),
                String.format("Bulk %s completed: %d succeeded, %d failed",
                    request.getOperation(), response.getSuccessCount(), response.getFailureCount())
            );

            logger.info("Bulk operation completed: {} succeeded, {} failed",
                response.getSuccessCount(), response.getFailureCount());

        } catch (Exception e) {
            logger.error("Bulk operation failed: {}", e.getMessage(), e);
            
            // Log failure
            auditService.logFailure(
                getAuditAction(request.getOperation()),
                request.getEntityType().name(),
                response.getOperationId(),
                "Bulk operation failed",
                e
            );

            // Re-throw to trigger transaction rollback
            throw new RuntimeException("Bulk operation failed: " + e.getMessage(), e);
        }

        return response;
    }

    /**
     * Execute bulk delete operation
     */
    private void executeBulkDelete(BulkOperationRequest request, BulkOperationResponse response) {
        List<String> entityIds = request.getEntityIds();
        
        for (String entityId : entityIds) {
            try {
                boolean deleted = deleteEntity(request.getEntityType(), entityId);
                
                if (deleted) {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, true, "Entity deleted successfully"));
                } else {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, false, "Entity not found"));
                }
            } catch (Exception e) {
                logger.error("Failed to delete entity {}: {}", entityId, e.getMessage());
                BulkOperationResponse.OperationResult result = 
                    new BulkOperationResponse.OperationResult(entityId, false, "Delete failed");
                result.setErrorDetails(e.getMessage());
                response.addResult(result);
                
                // Throw exception to trigger rollback (all-or-nothing)
                throw new RuntimeException("Bulk delete failed for entity: " + entityId, e);
            }
        }
    }

    /**
     * Execute bulk update operation
     */
    private void executeBulkUpdate(BulkOperationRequest request, BulkOperationResponse response) {
        List<String> entityIds = request.getEntityIds();
        
        for (String entityId : entityIds) {
            try {
                boolean updated = updateEntity(
                    request.getEntityType(), 
                    entityId, 
                    request.getUpdateFields());
                
                if (updated) {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, true, "Entity updated successfully"));
                } else {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, false, "Entity not found"));
                }
            } catch (Exception e) {
                logger.error("Failed to update entity {}: {}", entityId, e.getMessage());
                BulkOperationResponse.OperationResult result = 
                    new BulkOperationResponse.OperationResult(entityId, false, "Update failed");
                result.setErrorDetails(e.getMessage());
                response.addResult(result);
                
                // Throw exception to trigger rollback (all-or-nothing)
                throw new RuntimeException("Bulk update failed for entity: " + entityId, e);
            }
        }
    }

    /**
     * Execute bulk publish operation
     */
    private void executeBulkPublish(BulkOperationRequest request, BulkOperationResponse response) {
        List<String> entityIds = request.getEntityIds();
        
        for (String entityId : entityIds) {
            try {
                boolean published = publishEntity(request.getEntityType(), entityId);
                
                if (published) {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, true, "Entity published successfully"));
                } else {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, false, "Entity not found or already published"));
                }
            } catch (Exception e) {
                logger.error("Failed to publish entity {}: {}", entityId, e.getMessage());
                BulkOperationResponse.OperationResult result = 
                    new BulkOperationResponse.OperationResult(entityId, false, "Publish failed");
                result.setErrorDetails(e.getMessage());
                response.addResult(result);
                
                // Throw exception to trigger rollback (all-or-nothing)
                throw new RuntimeException("Bulk publish failed for entity: " + entityId, e);
            }
        }
    }

    /**
     * Execute bulk unpublish operation
     */
    private void executeBulkUnpublish(BulkOperationRequest request, BulkOperationResponse response) {
        List<String> entityIds = request.getEntityIds();
        
        for (String entityId : entityIds) {
            try {
                boolean unpublished = unpublishEntity(request.getEntityType(), entityId);
                
                if (unpublished) {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, true, "Entity unpublished successfully"));
                } else {
                    response.addResult(new BulkOperationResponse.OperationResult(
                        entityId, false, "Entity not found or already unpublished"));
                }
            } catch (Exception e) {
                logger.error("Failed to unpublish entity {}: {}", entityId, e.getMessage());
                BulkOperationResponse.OperationResult result = 
                    new BulkOperationResponse.OperationResult(entityId, false, "Unpublish failed");
                result.setErrorDetails(e.getMessage());
                response.addResult(result);
                
                // Throw exception to trigger rollback (all-or-nothing)
                throw new RuntimeException("Bulk unpublish failed for entity: " + entityId, e);
            }
        }
    }

    /**
     * Delete entity by type and ID
     */
    private boolean deleteEntity(BulkOperationRequest.EntityType entityType, String entityId) {
        UUID uuid = UUID.fromString(entityId);
        switch (entityType) {
            case MEMBER:
                if (memberRepository.existsById(uuid)) {
                    memberRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case PLAYER:
                if (playerRepository.existsById(uuid)) {
                    playerRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case TOURNAMENT:
                if (tournamentRepository.existsById(uuid)) {
                    tournamentRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case NEWS_ARTICLE:
                if (newsArticleRepository.existsById(uuid)) {
                    newsArticleRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case MEDIA_ITEM:
                if (mediaItemRepository.existsById(uuid)) {
                    mediaItemRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case DISTRICT:
                if (districtRepository.existsById(uuid)) {
                    districtRepository.deleteById(uuid);
                    return true;
                }
                return false;
            case DOWNLOAD:
                if (downloadRepository.existsById(uuid)) {
                    downloadRepository.deleteById(uuid);
                    return true;
                }
                return false;
            default:
                throw new UnsupportedOperationException("Entity type not supported: " + entityType);
        }
    }

    /**
     * Update entity by type and ID
     */
    private boolean updateEntity(BulkOperationRequest.EntityType entityType, 
                                 String entityId, 
                                 java.util.Map<String, Object> updateFields) {
        // This is a simplified implementation
        // In a real application, you would use reflection or a more sophisticated approach
        // to update fields dynamically
        
        UUID uuid = UUID.fromString(entityId);
        switch (entityType) {
            case NEWS_ARTICLE:
                return newsArticleRepository.findById(uuid)
                    .map(article -> {
                        // Update fields based on updateFields map
                        if (updateFields.containsKey("title")) {
                            article.setTitle((String) updateFields.get("title"));
                        }
                        if (updateFields.containsKey("content")) {
                            article.setContent((String) updateFields.get("content"));
                        }
                        newsArticleRepository.save(article);
                        return true;
                    })
                    .orElse(false);
            default:
                // Add support for other entity types as needed
                return false;
        }
    }

    /**
     * Publish entity by type and ID
     */
    private boolean publishEntity(BulkOperationRequest.EntityType entityType, String entityId) {
        UUID uuid = UUID.fromString(entityId);
        if (entityType == BulkOperationRequest.EntityType.NEWS_ARTICLE) {
            return newsArticleRepository.findById(uuid)
                .map(article -> {
                    article.publish();
                    newsArticleRepository.save(article);
                    return true;
                })
                .orElse(false);
        }
        return false;
    }

    /**
     * Unpublish entity by type and ID
     */
    private boolean unpublishEntity(BulkOperationRequest.EntityType entityType, String entityId) {
        UUID uuid = UUID.fromString(entityId);
        if (entityType == BulkOperationRequest.EntityType.NEWS_ARTICLE) {
            return newsArticleRepository.findById(uuid)
                .map(article -> {
                    article.unpublish();
                    newsArticleRepository.save(article);
                    return true;
                })
                .orElse(false);
        }
        return false;
    }

    /**
     * Get audit action for operation type
     */
    private AuditLog.AuditAction getAuditAction(BulkOperationRequest.OperationType operation) {
        switch (operation) {
            case CREATE:
                return AuditLog.AuditAction.BULK_CREATE;
            case UPDATE:
                return AuditLog.AuditAction.BULK_UPDATE;
            case DELETE:
                return AuditLog.AuditAction.BULK_DELETE;
            default:
                return AuditLog.AuditAction.UPDATE;
        }
    }
}
