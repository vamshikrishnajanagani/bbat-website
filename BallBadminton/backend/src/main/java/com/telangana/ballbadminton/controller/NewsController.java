package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.news.NewsArticleRequest;
import com.telangana.ballbadminton.dto.news.NewsArticleResponse;
import com.telangana.ballbadminton.dto.news.NewsCategoryRequest;
import com.telangana.ballbadminton.dto.news.NewsCategoryResponse;
import com.telangana.ballbadminton.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for News management
 */
@RestController
@RequestMapping("/api/v1/news")
@Tag(name = "News", description = "News and article management operations")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // News Article endpoints
    @GetMapping("/articles")
    @Operation(summary = "Get published articles with pagination")
    public ResponseEntity<Page<NewsArticleResponse>> getPublishedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsArticleResponse> articles = newsService.getPublishedArticles(page, size);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles/category/{categoryId}")
    @Operation(summary = "Get articles by category")
    public ResponseEntity<Page<NewsArticleResponse>> getArticlesByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsArticleResponse> articles = newsService.getArticlesByCategory(categoryId, page, size);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles/featured")
    @Operation(summary = "Get featured articles")
    public ResponseEntity<List<NewsArticleResponse>> getFeaturedArticles() {
        List<NewsArticleResponse> articles = newsService.getFeaturedArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles/slug/{slug}")
    @Operation(summary = "Get article by slug")
    public ResponseEntity<NewsArticleResponse> getArticleBySlug(@PathVariable String slug) {
        return newsService.getArticleBySlug(slug)
                .map(article -> ResponseEntity.ok(article))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/articles/search")
    @Operation(summary = "Search articles")
    public ResponseEntity<Page<NewsArticleResponse>> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsArticleResponse> articles = newsService.searchArticles(query, page, size);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles/recent")
    @Operation(summary = "Get recent articles")
    public ResponseEntity<List<NewsArticleResponse>> getRecentArticles(
            @RequestParam(defaultValue = "7") int days) {
        List<NewsArticleResponse> articles = newsService.getRecentArticles(days);
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/articles")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_CREATE')")
    @Operation(summary = "Create news article")
    public ResponseEntity<NewsArticleResponse> createArticle(@Valid @RequestBody NewsArticleRequest request) {
        NewsArticleResponse article = newsService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(article);
    }

    @PutMapping("/articles/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_UPDATE')")
    @Operation(summary = "Update news article")
    public ResponseEntity<NewsArticleResponse> updateArticle(
            @PathVariable UUID id,
            @Valid @RequestBody NewsArticleRequest request) {
        NewsArticleResponse article = newsService.updateArticle(id, request);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/articles/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_DELETE')")
    @Operation(summary = "Delete news article")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        newsService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // News Category endpoints
    @GetMapping("/categories")
    @Operation(summary = "Get all active categories")
    public ResponseEntity<List<NewsCategoryResponse>> getAllActiveCategories() {
        List<NewsCategoryResponse> categories = newsService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/slug/{slug}")
    @Operation(summary = "Get category by slug")
    public ResponseEntity<NewsCategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return newsService.getCategoryBySlug(slug)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_CATEGORY_CREATE')")
    @Operation(summary = "Create news category")
    public ResponseEntity<NewsCategoryResponse> createCategory(@Valid @RequestBody NewsCategoryRequest request) {
        NewsCategoryResponse category = newsService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_CATEGORY_UPDATE')")
    @Operation(summary = "Update news category")
    public ResponseEntity<NewsCategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody NewsCategoryRequest request) {
        NewsCategoryResponse category = newsService.updateCategory(id, request);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_CATEGORY_DELETE')")
    @Operation(summary = "Delete news category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        newsService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Announcement endpoints
    @GetMapping("/announcements")
    @Operation(summary = "Get active announcements")
    public ResponseEntity<List<NewsArticleResponse>> getActiveAnnouncements() {
        List<NewsArticleResponse> announcements = newsService.getActiveAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/announcements/priority")
    @Operation(summary = "Get high priority announcements")
    public ResponseEntity<List<NewsArticleResponse>> getHighPriorityAnnouncements() {
        List<NewsArticleResponse> announcements = newsService.getHighPriorityAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @PostMapping("/articles/{id}/publish")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_UPDATE')")
    @Operation(summary = "Publish article and send notifications")
    public ResponseEntity<NewsArticleResponse> publishArticle(@PathVariable UUID id) {
        NewsArticleResponse article = newsService.publishArticle(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping("/articles/{id}/unpublish")
    @PreAuthorize("hasAuthority('PERMISSION_NEWS_UPDATE')")
    @Operation(summary = "Unpublish article")
    public ResponseEntity<NewsArticleResponse> unpublishArticle(@PathVariable UUID id) {
        NewsArticleResponse article = newsService.unpublishArticle(id);
        return ResponseEntity.ok(article);
    }
}