package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.dto.admin.BulkOperationRequest;
import com.telangana.ballbadminton.dto.admin.BulkOperationResponse;
import com.telangana.ballbadminton.entity.AuditLog;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BulkOperationService
 * 
 * Tests bulk operations on content entities including:
 * - Bulk delete operations
 * - Bulk update operations
 * - Bulk publish/unpublish operations
 * - Transaction rollback on failure (atomicity)
 * 
 * Requirements: 6.3, 9.5
 * Property 17: Bulk Operation Atomicity
 */
@DisplayName("BulkOperationService Tests")
class BulkOperationServiceTest extends BaseUnitTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @Mock
    private MediaItemRepository mediaItemRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private DownloadRepository downloadRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BulkOperationService bulkOperationService;

    private NewsArticle testArticle1;
    private NewsArticle testArticle2;

    @BeforeEach
    void setupTest() {
        testArticle1 = new NewsArticle();
        testArticle1.setId("article-1");
        testArticle1.setTitle("Test Article 1");
        testArticle1.setIsPublished(false);

        testArticle2 = new NewsArticle();
        testArticle2.setId("article-2");
        testArticle2.setTitle("Test Article 2");
        testArticle2.setIsPublished(false);
    }

    @Test
    @DisplayName("Should successfully execute bulk delete operation")
    void testBulkDelete_Success() {
        // Arrange
        List<String> entityIds = Arrays.asList("article-1", "article-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.existsById("article-1")).thenReturn(true);
        when(newsArticleRepository.existsById("article-2")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOperation()).isEqualTo(BulkOperationRequest.OperationType.DELETE);
        assertThat(response.getEntityType()).isEqualTo(BulkOperationRequest.EntityType.NEWS_ARTICLE);
        assertThat(response.getTotalCount()).isEqualTo(2);
        assertThat(response.getSuccessCount()).isEqualTo(2);
        assertThat(response.getFailureCount()).isEqualTo(0);

        verify(newsArticleRepository).deleteById("article-1");
        verify(newsArticleRepository).deleteById("article-2");
        verify(auditService).audit(
            eq(AuditLog.AuditAction.BULK_DELETE),
            eq("NEWS_ARTICLE"),
            anyString(),
            anyString()
        );
    }

    @Test
    @DisplayName("Should rollback bulk delete when one entity fails")
    void testBulkDelete_RollbackOnFailure() {
        // Arrange
        List<String> entityIds = Arrays.asList("article-1", "article-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.existsById("article-1")).thenReturn(true);
        when(newsArticleRepository.existsById("article-2")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bulkOperationService.executeBulkOperation(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Bulk delete failed");

        verify(auditService).logFailure(
            eq(AuditLog.AuditAction.BULK_DELETE),
            eq("NEWS_ARTICLE"),
            anyString(),
            anyString(),
            any(Exception.class)
        );
    }

    @Test
    @DisplayName("Should successfully execute bulk publish operation")
    void testBulkPublish_Success() {
        // Arrange
        List<String> entityIds = Arrays.asList("article-1", "article-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.PUBLISH,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle1));
        when(newsArticleRepository.findById("article-2")).thenReturn(Optional.of(testArticle2));
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSuccessCount()).isEqualTo(2);
        assertThat(response.getFailureCount()).isEqualTo(0);
        assertThat(testArticle1.getIsPublished()).isTrue();
        assertThat(testArticle2.getIsPublished()).isTrue();

        verify(newsArticleRepository, times(2)).save(any(NewsArticle.class));
    }

    @Test
    @DisplayName("Should successfully execute bulk unpublish operation")
    void testBulkUnpublish_Success() {
        // Arrange
        testArticle1.setIsPublished(true);
        testArticle2.setIsPublished(true);

        List<String> entityIds = Arrays.asList("article-1", "article-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.UNPUBLISH,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.findById("article-1")).thenReturn(Optional.of(testArticle1));
        when(newsArticleRepository.findById("article-2")).thenReturn(Optional.of(testArticle2));
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSuccessCount()).isEqualTo(2);
        assertThat(response.getFailureCount()).isEqualTo(0);
        assertThat(testArticle1.getIsPublished()).isFalse();
        assertThat(testArticle2.getIsPublished()).isFalse();

        verify(newsArticleRepository, times(2)).save(any(NewsArticle.class));
    }

    @Test
    @DisplayName("Should handle entity not found in bulk operation")
    void testBulkOperation_EntityNotFound() {
        // Arrange
        List<String> entityIds = Arrays.asList("non-existent-id");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.existsById("non-existent-id")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bulkOperationService.executeBulkOperation(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Bulk delete failed");
    }

    @Test
    @DisplayName("Should delete members in bulk operation")
    void testBulkDelete_Members() {
        // Arrange
        List<String> entityIds = Arrays.asList("member-1", "member-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.MEMBER
        );
        request.setEntityIds(entityIds);

        when(memberRepository.existsById("member-1")).thenReturn(true);
        when(memberRepository.existsById("member-2")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response.getSuccessCount()).isEqualTo(2);
        verify(memberRepository).deleteById("member-1");
        verify(memberRepository).deleteById("member-2");
    }

    @Test
    @DisplayName("Should delete players in bulk operation")
    void testBulkDelete_Players() {
        // Arrange
        List<String> entityIds = Arrays.asList("player-1", "player-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.PLAYER
        );
        request.setEntityIds(entityIds);

        when(playerRepository.existsById("player-1")).thenReturn(true);
        when(playerRepository.existsById("player-2")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response.getSuccessCount()).isEqualTo(2);
        verify(playerRepository).deleteById("player-1");
        verify(playerRepository).deleteById("player-2");
    }

    @Test
    @DisplayName("Should delete tournaments in bulk operation")
    void testBulkDelete_Tournaments() {
        // Arrange
        List<String> entityIds = Arrays.asList("tournament-1", "tournament-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.TOURNAMENT
        );
        request.setEntityIds(entityIds);

        when(tournamentRepository.existsById("tournament-1")).thenReturn(true);
        when(tournamentRepository.existsById("tournament-2")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response.getSuccessCount()).isEqualTo(2);
        verify(tournamentRepository).deleteById("tournament-1");
        verify(tournamentRepository).deleteById("tournament-2");
    }

    @Test
    @DisplayName("Should include operation ID in response")
    void testBulkOperation_IncludesOperationId() {
        // Arrange
        List<String> entityIds = Arrays.asList("article-1");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.existsById("article-1")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response.getOperationId()).isNotNull();
        assertThat(response.getOperationId()).isNotEmpty();
    }

    @Test
    @DisplayName("Should include detailed results for each entity")
    void testBulkOperation_DetailedResults() {
        // Arrange
        List<String> entityIds = Arrays.asList("article-1", "article-2");
        BulkOperationRequest request = new BulkOperationRequest(
            BulkOperationRequest.OperationType.DELETE,
            BulkOperationRequest.EntityType.NEWS_ARTICLE
        );
        request.setEntityIds(entityIds);

        when(newsArticleRepository.existsById("article-1")).thenReturn(true);
        when(newsArticleRepository.existsById("article-2")).thenReturn(true);

        // Act
        BulkOperationResponse response = bulkOperationService.executeBulkOperation(request);

        // Assert
        assertThat(response.getResults()).hasSize(2);
        assertThat(response.getResults().get(0).getEntityId()).isEqualTo("article-1");
        assertThat(response.getResults().get(0).isSuccess()).isTrue();
        assertThat(response.getResults().get(1).getEntityId()).isEqualTo("article-2");
        assertThat(response.getResults().get(1).isSuccess()).isTrue();
    }
}
