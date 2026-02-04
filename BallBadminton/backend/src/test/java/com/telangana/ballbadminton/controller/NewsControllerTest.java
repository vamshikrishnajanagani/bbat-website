package com.telangana.ballbadminton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telangana.ballbadminton.dto.news.NewsArticleRequest;
import com.telangana.ballbadminton.dto.news.NewsArticleResponse;
import com.telangana.ballbadminton.dto.news.NewsCategoryRequest;
import com.telangana.ballbadminton.dto.news.NewsCategoryResponse;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NewsController
 */
@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewsArticleResponse testArticleResponse;
    private NewsArticleRequest testArticleRequest;
    private NewsCategoryResponse testCategoryResponse;
    private NewsCategoryRequest testCategoryRequest;
    private UUID testArticleId;
    private UUID testCategoryId;

    @BeforeEach
    void setUp() {
        testArticleId = UUID.randomUUID();
        testCategoryId = UUID.randomUUID();
        
        // Setup category
        testCategoryResponse = new NewsCategoryResponse();
        testCategoryResponse.setId(testCategoryId);
        testCategoryResponse.setName("Tournament News");
        testCategoryResponse.setSlug("tournament-news");
        testCategoryResponse.setDescription("News about tournaments");
        testCategoryResponse.setIsActive(true);
        testCategoryResponse.setArticleCount(5L);
        testCategoryResponse.setCreatedAt(LocalDateTime.now());
        testCategoryResponse.setUpdatedAt(LocalDateTime.now());

        testCategoryRequest = new NewsCategoryRequest();
        testCategoryRequest.setName("Tournament News");
        testCategoryRequest.setSlug("tournament-news");
        testCategoryRequest.setDescription("News about tournaments");
        testCategoryRequest.setIsActive(true);

        // Setup article
        testArticleResponse = new NewsArticleResponse();
        testArticleResponse.setId(testArticleId);
        testArticleResponse.setTitle("State Championship Announced");
        testArticleResponse.setSlug("state-championship-announced");
        testArticleResponse.setSummary("Annual state championship will be held in December");
        testArticleResponse.setContent("The Telangana Ball Badminton Association announces...");
        testArticleResponse.setFeaturedImageUrl("/images/championship.jpg");
        testArticleResponse.setAuthor("Sports Editor");
        testArticleResponse.setPublishedAt(LocalDateTime.now());
        testArticleResponse.setIsPublished(true);
        testArticleResponse.setIsFeatured(true);
        testArticleResponse.setViewCount(150);
        testArticleResponse.setLanguage(NewsArticle.Language.ENGLISH);
        testArticleResponse.setCategory(testCategoryResponse);
        testArticleResponse.setCreatedAt(LocalDateTime.now());
        testArticleResponse.setUpdatedAt(LocalDateTime.now());

        testArticleRequest = new NewsArticleRequest();
        testArticleRequest.setTitle("State Championship Announced");
        testArticleRequest.setSlug("state-championship-announced");
        testArticleRequest.setSummary("Annual state championship will be held in December");
        testArticleRequest.setContent("The Telangana Ball Badminton Association announces...");
        testArticleRequest.setFeaturedImageUrl("/images/championship.jpg");
        testArticleRequest.setAuthor("Sports Editor");
        testArticleRequest.setPublishedAt(LocalDateTime.now());
        testArticleRequest.setIsPublished(true);
        testArticleRequest.setIsFeatured(true);
        testArticleRequest.setLanguage(NewsArticle.Language.ENGLISH);
        testArticleRequest.setCategoryId(testCategoryId);
    }

    // News Article Tests
    @Test
    void getPublishedArticles_ShouldReturnPublishedArticles() throws Exception {
        // Given
        List<NewsArticleResponse> articles = Arrays.asList(testArticleResponse);
        Page<NewsArticleResponse> articlePage = new PageImpl<>(articles);
        when(newsService.getPublishedArticles(0, 10)).thenReturn(articlePage);

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("State Championship Announced"))
                .andExpect(jsonPath("$.content[0].isPublished").value(true));

        verify(newsService).getPublishedArticles(0, 10);
    }

    @Test
    void getArticlesByCategory_ShouldReturnArticlesByCategory() throws Exception {
        // Given
        List<NewsArticleResponse> articles = Arrays.asList(testArticleResponse);
        Page<NewsArticleResponse> articlePage = new PageImpl<>(articles);
        when(newsService.getArticlesByCategory(testCategoryId, 0, 10)).thenReturn(articlePage);

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/category/{categoryId}", testCategoryId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category.name").value("Tournament News"));

        verify(newsService).getArticlesByCategory(testCategoryId, 0, 10);
    }

    @Test
    void getFeaturedArticles_ShouldReturnFeaturedArticles() throws Exception {
        // Given
        List<NewsArticleResponse> articles = Arrays.asList(testArticleResponse);
        when(newsService.getFeaturedArticles()).thenReturn(articles);

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/featured"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isFeatured").value(true));

        verify(newsService).getFeaturedArticles();
    }

    @Test
    void getArticleBySlug_WhenArticleExists_ShouldReturnArticle() throws Exception {
        // Given
        when(newsService.getArticleBySlug("state-championship-announced"))
                .thenReturn(Optional.of(testArticleResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/slug/{slug}", "state-championship-announced"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("state-championship-announced"))
                .andExpect(jsonPath("$.title").value("State Championship Announced"));

        verify(newsService).getArticleBySlug("state-championship-announced");
    }

    @Test
    void getArticleBySlug_WhenArticleNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(newsService.getArticleBySlug("non-existent-slug")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/slug/{slug}", "non-existent-slug"))
                .andExpect(status().isNotFound());

        verify(newsService).getArticleBySlug("non-existent-slug");
    }

    @Test
    void searchArticles_ShouldReturnMatchingArticles() throws Exception {
        // Given
        List<NewsArticleResponse> articles = Arrays.asList(testArticleResponse);
        Page<NewsArticleResponse> articlePage = new PageImpl<>(articles);
        when(newsService.searchArticles("championship", 0, 10)).thenReturn(articlePage);

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/search")
                .param("query", "championship")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("State Championship Announced"));

        verify(newsService).searchArticles("championship", 0, 10);
    }

    @Test
    void getRecentArticles_ShouldReturnRecentArticles() throws Exception {
        // Given
        List<NewsArticleResponse> articles = Arrays.asList(testArticleResponse);
        when(newsService.getRecentArticles(7)).thenReturn(articles);

        // When & Then
        mockMvc.perform(get("/api/v1/news/articles/recent")
                .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("State Championship Announced"));

        verify(newsService).getRecentArticles(7);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CREATE")
    void createArticle_WithValidData_ShouldCreateArticle() throws Exception {
        // Given
        when(newsService.createArticle(any(NewsArticleRequest.class))).thenReturn(testArticleResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/news/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArticleRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("State Championship Announced"))
                .andExpect(jsonPath("$.slug").value("state-championship-announced"));

        verify(newsService).createArticle(any(NewsArticleRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CREATE")
    void createArticle_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        NewsArticleRequest invalidRequest = new NewsArticleRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/news/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(newsService, never()).createArticle(any(NewsArticleRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_UPDATE")
    void updateArticle_WithValidData_ShouldUpdateArticle() throws Exception {
        // Given
        when(newsService.updateArticle(eq(testArticleId), any(NewsArticleRequest.class)))
                .thenReturn(testArticleResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/news/articles/{id}", testArticleId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArticleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("State Championship Announced"));

        verify(newsService).updateArticle(eq(testArticleId), any(NewsArticleRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_DELETE")
    void deleteArticle_ShouldDeleteArticle() throws Exception {
        // Given
        doNothing().when(newsService).deleteArticle(testArticleId);

        // When & Then
        mockMvc.perform(delete("/api/v1/news/articles/{id}", testArticleId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(newsService).deleteArticle(testArticleId);
    }

    // News Category Tests
    @Test
    void getAllActiveCategories_ShouldReturnAllActiveCategories() throws Exception {
        // Given
        List<NewsCategoryResponse> categories = Arrays.asList(testCategoryResponse);
        when(newsService.getAllActiveCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/v1/news/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Tournament News"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(newsService).getAllActiveCategories();
    }

    @Test
    void getCategoryBySlug_WhenCategoryExists_ShouldReturnCategory() throws Exception {
        // Given
        when(newsService.getCategoryBySlug("tournament-news"))
                .thenReturn(Optional.of(testCategoryResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/news/categories/slug/{slug}", "tournament-news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("tournament-news"))
                .andExpect(jsonPath("$.name").value("Tournament News"));

        verify(newsService).getCategoryBySlug("tournament-news");
    }

    @Test
    void getCategoryBySlug_WhenCategoryNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(newsService.getCategoryBySlug("non-existent-slug")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/news/categories/slug/{slug}", "non-existent-slug"))
                .andExpect(status().isNotFound());

        verify(newsService).getCategoryBySlug("non-existent-slug");
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CATEGORY_CREATE")
    void createCategory_WithValidData_ShouldCreateCategory() throws Exception {
        // Given
        when(newsService.createCategory(any(NewsCategoryRequest.class))).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/news/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Tournament News"))
                .andExpect(jsonPath("$.slug").value("tournament-news"));

        verify(newsService).createCategory(any(NewsCategoryRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CATEGORY_UPDATE")
    void updateCategory_WithValidData_ShouldUpdateCategory() throws Exception {
        // Given
        when(newsService.updateCategory(eq(testCategoryId), any(NewsCategoryRequest.class)))
                .thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/news/categories/{id}", testCategoryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Tournament News"));

        verify(newsService).updateCategory(eq(testCategoryId), any(NewsCategoryRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CATEGORY_DELETE")
    void deleteCategory_ShouldDeleteCategory() throws Exception {
        // Given
        doNothing().when(newsService).deleteCategory(testCategoryId);

        // When & Then
        mockMvc.perform(delete("/api/v1/news/categories/{id}", testCategoryId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(newsService).deleteCategory(testCategoryId);
    }

    // Permission Tests
    @Test
    void createArticle_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/news/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArticleRequest)))
                .andExpect(status().isForbidden());

        verify(newsService, never()).createArticle(any(NewsArticleRequest.class));
    }

    @Test
    void updateArticle_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/news/articles/{id}", testArticleId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArticleRequest)))
                .andExpect(status().isForbidden());

        verify(newsService, never()).updateArticle(any(UUID.class), any(NewsArticleRequest.class));
    }

    @Test
    void deleteArticle_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/news/articles/{id}", testArticleId)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(newsService, never()).deleteArticle(any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_NEWS_CREATE")
    void createArticle_WithDuplicateSlug_ShouldReturnBadRequest() throws Exception {
        // Given
        when(newsService.createArticle(any(NewsArticleRequest.class)))
                .thenThrow(new IllegalArgumentException("Article with slug state-championship-announced already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/news/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArticleRequest)))
                .andExpect(status().isBadRequest());

        verify(newsService).createArticle(any(NewsArticleRequest.class));
    }
}