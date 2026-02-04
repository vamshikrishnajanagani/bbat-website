package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.admin.BulkOperationRequest;
import com.telangana.ballbadminton.dto.admin.SchedulePublicationRequest;
import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.repository.NewsArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for content scheduling and publication workflows
 * 
 * Provides functionality to schedule content for future publication
 * and automatically publish content at scheduled times
 * 
 * Requirements: 6.4
 * Property 18: Scheduled Publication Timing
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
public class ContentSchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(ContentSchedulingService.class);

    private final NewsArticleRepository newsArticleRepository;
    private final AuditService auditService;

    public ContentSchedulingService(
            NewsArticleRepository newsArticleRepository,
            AuditService auditService) {
        this.newsArticleRepository = newsArticleRepository;
        this.auditService = auditService;
    }

    /**
     * Schedule content for future publication
     * 
     * @param request Schedule publication request
     * @return true if scheduled successfully
     */
    @Transactional
    public boolean schedulePublication(SchedulePublicationRequest request) {
        logger.info("Scheduling publication for {} with ID {} at {}", 
            request.getEntityType(), 
            request.getEntityId(), 
            request.getScheduledPublicationDate());

        try {
            if (request.getEntityType() == BulkOperationRequest.EntityType.NEWS_ARTICLE) {
                UUID entityId = UUID.fromString(request.getEntityId());
                return newsArticleRepository.findById(entityId)
                    .map(article -> {
                        article.setScheduledPublicationDate(request.getScheduledPublicationDate());
                        article.setIsPublished(false); // Ensure it's not published yet
                        newsArticleRepository.save(article);

                        auditService.audit(
                            AuditLog.AuditAction.UPDATE,
                            "NewsArticle",
                            request.getEntityId(),
                            String.format("Scheduled for publication at %s", 
                                request.getScheduledPublicationDate())
                        );

                        logger.info("Successfully scheduled publication for article: {}", 
                            request.getEntityId());
                        return true;
                    })
                    .orElse(false);
            }

            return false;

        } catch (Exception e) {
            logger.error("Failed to schedule publication: {}", e.getMessage(), e);
            
            auditService.logFailure(
                AuditLog.AuditAction.UPDATE,
                request.getEntityType().name(),
                request.getEntityId(),
                "Failed to schedule publication",
                e
            );

            throw new RuntimeException("Failed to schedule publication", e);
        }
    }

    /**
     * Cancel scheduled publication
     * 
     * @param entityType Entity type
     * @param entityId Entity ID
     * @return true if cancelled successfully
     */
    @Transactional
    public boolean cancelScheduledPublication(BulkOperationRequest.EntityType entityType, 
                                             String entityId) {
        logger.info("Cancelling scheduled publication for {} with ID {}", entityType, entityId);

        try {
            if (entityType == BulkOperationRequest.EntityType.NEWS_ARTICLE) {
                UUID uuid = UUID.fromString(entityId);
                return newsArticleRepository.findById(uuid)
                    .map(article -> {
                        article.setScheduledPublicationDate(null);
                        newsArticleRepository.save(article);

                        auditService.audit(
                            AuditLog.AuditAction.UPDATE,
                            "NewsArticle",
                            entityId,
                            "Cancelled scheduled publication"
                        );

                        logger.info("Successfully cancelled scheduled publication for article: {}", 
                            entityId);
                        return true;
                    })
                    .orElse(false);
            }

            return false;

        } catch (Exception e) {
            logger.error("Failed to cancel scheduled publication: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to cancel scheduled publication", e);
        }
    }

    /**
     * Get all scheduled publications
     * 
     * @return List of scheduled news articles
     */
    public List<NewsArticle> getScheduledPublications() {
        return newsArticleRepository.findByScheduledPublicationDateIsNotNullAndIsPublishedFalse();
    }

    /**
     * Process scheduled publications - runs every minute
     * 
     * Automatically publishes content that has reached its scheduled publication time
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledPublications() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Find articles scheduled for publication before or at current time
            List<NewsArticle> articlesToPublish = newsArticleRepository
                .findByScheduledPublicationDateBeforeAndIsPublishedFalse(now);

            if (!articlesToPublish.isEmpty()) {
                logger.info("Processing {} scheduled publications", articlesToPublish.size());

                for (NewsArticle article : articlesToPublish) {
                    try {
                        article.publish();
                        article.setScheduledPublicationDate(null); // Clear scheduled date
                        newsArticleRepository.save(article);

                        auditService.audit(
                            AuditLog.AuditAction.UPDATE,
                            "NewsArticle",
                            article.getId().toString(),
                            "Automatically published via scheduled publication"
                        );

                        logger.info("Successfully published scheduled article: {} - {}", 
                            article.getId(), article.getTitle());

                    } catch (Exception e) {
                        logger.error("Failed to publish scheduled article {}: {}", 
                            article.getId(), e.getMessage(), e);
                        
                        auditService.logFailure(
                            AuditLog.AuditAction.UPDATE,
                            "NewsArticle",
                            article.getId().toString(),
                            "Failed to auto-publish scheduled article",
                            e
                        );
                    }
                }

                logger.info("Completed processing scheduled publications");
            }

        } catch (Exception e) {
            logger.error("Error processing scheduled publications: {}", e.getMessage(), e);
        }
    }

    /**
     * Get count of pending scheduled publications
     * 
     * @return Count of articles scheduled for future publication
     */
    public long getPendingScheduledCount() {
        return newsArticleRepository.countByScheduledPublicationDateIsNotNullAndIsPublishedFalse();
    }

    /**
     * Get scheduled publications for a specific date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of scheduled articles in date range
     */
    public List<NewsArticle> getScheduledPublicationsByDateRange(
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        return newsArticleRepository
            .findByScheduledPublicationDateBetweenAndIsPublishedFalse(startDate, endDate);
    }
}
