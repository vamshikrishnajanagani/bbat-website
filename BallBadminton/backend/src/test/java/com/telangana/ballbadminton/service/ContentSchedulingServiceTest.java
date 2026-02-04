package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.dto.admin.BulkOperationRequest;
import com.telangana.ballbadminton.dto.admin.SchedulePublicationRequest;
import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.repository.NewsArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContentSchedulingService
 * 
 * Tests content scheduling and publication workflows including:
 * - Scheduling content for future publication
 * - Cancelling scheduled publications
 * - Automatic publication processing
 * - Scheduled publication queries
 * 
 * Requirements: 6.4
 * Property 18: Scheduled Publication Timing
 */
@DisplayName("ContentSchedulingService Tests")
class ContentSchedulingServiceTest extends BaseUnitTest {

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ContentSchedulingService contentSchedulingService;

    private NewsArticle testArticle;
    private LocalDateTime futureDate;
    private LocalDateTime pastDate;

    @BeforeEach
    void setupTest() {
        testArticle = new NewsArticle();
        testArticle.setId("article-1");
        testArticle.setTitle("Test Article");
        testArticle.setIsPublished(false);

        futureDate = LocalDateTime.now().plusDays(1);
        pastDate = LocalDateTime.now().minusHours(1);
    }

    @Test
    @DisplayName("Should successfully schedule publication for future date")
    void testSchedulePublication_Success() {
        // Arrange
        SchedulePublicationRequest request = new SchedulePublicationRequest(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1",
            futureDate
        );

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle));
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = contentSchedulingService.schedulePublication(request);

        // Assert
        assertThat(result).isTrue();
        assertThat(testArticle.getScheduledPublicationDate()).isEqualTo(futureDate);
        assertThat(testArticle.getIsPublished()).isFalse();

        verify(newsArticleRepository).save(testArticle);
        verify(auditService).audit(
            eq(AuditLog.AuditAction.UPDATE),
            eq("NewsArticle"),
            eq("article-1"),
            contains("Scheduled for publication")
        );
    }

    @Test
    @DisplayName("Should return false when scheduling publication for non-existent article")
    void testSchedulePublication_ArticleNotFound() {
        // Arrange
        SchedulePublicationRequest request = new SchedulePublicationRequest(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "non-existent-id",
            futureDate
        );

        when(newsArticleRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act
        boolean result = contentSchedulingService.schedulePublication(request);

        // Assert
        assertThat(result).isFalse();
        verify(newsArticleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully cancel scheduled publication")
    void testCancelScheduledPublication_Success() {
        // Arrange
        testArticle.setScheduledPublicationDate(futureDate);

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle));
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = contentSchedulingService.cancelScheduledPublication(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1"
        );

        // Assert
        assertThat(result).isTrue();
        assertThat(testArticle.getScheduledPublicationDate()).isNull();

        verify(newsArticleRepository).save(testArticle);
        verify(auditService).audit(
            eq(AuditLog.AuditAction.UPDATE),
            eq("NewsArticle"),
            eq("article-1"),
            eq("Cancelled scheduled publication")
        );
    }

    @Test
    @DisplayName("Should return false when cancelling non-existent scheduled publication")
    void testCancelScheduledPublication_ArticleNotFound() {
        // Arrange
        when(newsArticleRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act
        boolean result = contentSchedulingService.cancelScheduledPublication(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "non-existent-id"
        );

        // Assert
        assertThat(result).isFalse();
        verify(newsArticleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve all scheduled publications")
    void testGetScheduledPublications() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setId("article-1");
        article1.setScheduledPublicationDate(futureDate);
        article1.setIsPublished(false);

        NewsArticle article2 = new NewsArticle();
        article2.setId("article-2");
        article2.setScheduledPublicationDate(futureDate.plusDays(1));
        article2.setIsPublished(false);

        List<NewsArticle> scheduledArticles = Arrays.asList(article1, article2);

        when(newsArticleRepository.findByScheduledPublicationDateIsNotNullAndIsPublishedFalse())
            .thenReturn(scheduledArticles);

        // Act
        List<NewsArticle> result = contentSchedulingService.getScheduledPublications();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(article1, article2);
    }

    @Test
    @DisplayName("Should process scheduled publications that are due")
    void testProcessScheduledPublications_PublishDueArticles() {
        // Arrange
        NewsArticle dueArticle1 = new NewsArticle();
        dueArticle1.setId("article-1");
        dueArticle1.setTitle("Due Article 1");
        dueArticle1.setScheduledPublicationDate(pastDate);
        dueArticle1.setIsPublished(false);

        NewsArticle dueArticle2 = new NewsArticle();
        dueArticle2.setId("article-2");
        dueArticle2.setTitle("Due Article 2");
        dueArticle2.setScheduledPublicationDate(pastDate);
        dueArticle2.setIsPublished(false);

        List<NewsArticle> dueArticles = Arrays.asList(dueArticle1, dueArticle2);

        when(newsArticleRepository.findByScheduledPublicationDateBeforeAndIsPublishedFalse(any(LocalDateTime.class)))
            .thenReturn(dueArticles);
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        contentSchedulingService.processScheduledPublications();

        // Assert
        assertThat(dueArticle1.getIsPublished()).isTrue();
        assertThat(dueArticle1.getScheduledPublicationDate()).isNull();
        assertThat(dueArticle2.getIsPublished()).isTrue();
        assertThat(dueArticle2.getScheduledPublicationDate()).isNull();

        verify(newsArticleRepository, times(2)).save(any(NewsArticle.class));
        verify(auditService, times(2)).audit(
            eq(AuditLog.AuditAction.UPDATE),
            eq("NewsArticle"),
            anyString(),
            contains("Automatically published")
        );
    }

    @Test
    @DisplayName("Should not process articles scheduled for future")
    void testProcessScheduledPublications_IgnoreFutureArticles() {
        // Arrange
        when(newsArticleRepository.findByScheduledPublicationDateBeforeAndIsPublishedFalse(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // Act
        contentSchedulingService.processScheduledPublications();

        // Assert
        verify(newsArticleRepository, never()).save(any());
        verify(auditService, never()).audit(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should handle errors during scheduled publication processing")
    void testProcessScheduledPublications_HandleErrors() {
        // Arrange
        NewsArticle dueArticle = new NewsArticle();
        dueArticle.setId("article-1");
        dueArticle.setScheduledPublicationDate(pastDate);
        dueArticle.setIsPublished(false);

        when(newsArticleRepository.findByScheduledPublicationDateBeforeAndIsPublishedFalse(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(dueArticle));
        when(newsArticleRepository.save(any(NewsArticle.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act - should not throw exception
        contentSchedulingService.processScheduledPublications();

        // Assert
        verify(auditService).logFailure(
            eq(AuditLog.AuditAction.UPDATE),
            eq("NewsArticle"),
            eq("article-1"),
            contains("Failed to auto-publish"),
            any(Exception.class)
        );
    }

    @Test
    @DisplayName("Should get count of pending scheduled publications")
    void testGetPendingScheduledCount() {
        // Arrange
        when(newsArticleRepository.countByScheduledPublicationDateIsNotNullAndIsPublishedFalse())
            .thenReturn(5L);

        // Act
        long count = contentSchedulingService.getPendingScheduledCount();

        // Assert
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should get scheduled publications by date range")
    void testGetScheduledPublicationsByDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        NewsArticle article1 = new NewsArticle();
        article1.setId("article-1");
        article1.setScheduledPublicationDate(startDate.plusDays(1));

        NewsArticle article2 = new NewsArticle();
        article2.setId("article-2");
        article2.setScheduledPublicationDate(startDate.plusDays(3));

        List<NewsArticle> articles = Arrays.asList(article1, article2);

        when(newsArticleRepository.findByScheduledPublicationDateBetweenAndIsPublishedFalse(startDate, endDate))
            .thenReturn(articles);

        // Act
        List<NewsArticle> result = contentSchedulingService.getScheduledPublicationsByDateRange(startDate, endDate);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(article1, article2);
    }

    @Test
    @DisplayName("Should ensure article is unpublished when scheduling")
    void testSchedulePublication_EnsuresUnpublished() {
        // Arrange
        testArticle.setIsPublished(true); // Already published

        SchedulePublicationRequest request = new SchedulePublicationRequest(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1",
            futureDate
        );

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle));
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = contentSchedulingService.schedulePublication(request);

        // Assert
        assertThat(result).isTrue();
        assertThat(testArticle.getIsPublished()).isFalse(); // Should be unpublished
        assertThat(testArticle.getScheduledPublicationDate()).isEqualTo(futureDate);
    }

    @Test
    @DisplayName("Should handle exception during schedule publication")
    void testSchedulePublication_HandlesException() {
        // Arrange
        SchedulePublicationRequest request = new SchedulePublicationRequest(
            BulkOperationRequest.EntityType.NEWS_ARTICLE,
            "article-1",
            futureDate
        );

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle));
        when(newsArticleRepository.save(any(NewsArticle.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> contentSchedulingService.schedulePublication(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to schedule publication");

        verify(auditService).logFailure(
            eq(AuditLog.AuditAction.UPDATE),
            eq("NEWS_ARTICLE"),
            eq("article-1"),
            eq("Failed to schedule publication"),
            any(Exception.class)
        );
    }
}
