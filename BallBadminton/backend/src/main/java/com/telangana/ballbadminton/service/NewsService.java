package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.news.NewsArticleRequest;
import com.telangana.ballbadminton.dto.news.NewsArticleResponse;
import com.telangana.ballbadminton.dto.news.NewsCategoryRequest;
import com.telangana.ballbadminton.dto.news.NewsCategoryResponse;
import com.telangana.ballbadminton.entity.NewsArticle;
import com.telangana.ballbadminton.entity.NewsCategory;
import com.telangana.ballbadminton.repository.NewsArticleRepository;
import com.telangana.ballbadminton.repository.NewsCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for News management
 */
@Service
@Transactional
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    @Autowired
    public NewsService(NewsArticleRepository newsArticleRepository, 
                      NewsCategoryRepository newsCategoryRepository) {
        this.newsArticleRepository = newsArticleRepository;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    // News Article methods
    /**
     * Get published articles with pagination
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> getPublishedArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsArticleRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable)
                .map(this::convertArticleToResponse);
    }

    /**
     * Get articles by category
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> getArticlesByCategory(UUID categoryId, int page, int size) {
        NewsCategory category = newsCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        
        Pageable pageable = PageRequest.of(page, size);
        return newsArticleRepository.findByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(category, pageable)
                .map(this::convertArticleToResponse);
    }

    /**
     * Get featured articles
     */
    @Cacheable(value = "news", key = "'featured'")
    @Transactional(readOnly = true)
    public List<NewsArticleResponse> getFeaturedArticles() {
        return newsArticleRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .map(this::convertArticleToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get article by slug
     */
    @Transactional(readOnly = true)
    public Optional<NewsArticleResponse> getArticleBySlug(String slug) {
        Optional<NewsArticle> article = newsArticleRepository.findBySlugAndIsPublishedTrue(slug);
        if (article.isPresent()) {
            // Increment view count
            NewsArticle newsArticle = article.get();
            newsArticle.incrementViewCount();
            newsArticleRepository.save(newsArticle);
        }
        return article.map(this::convertArticleToResponse);
    }

    /**
     * Search articles
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> searchArticles(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsArticleRepository.searchPublishedArticles(query, pageable)
                .map(this::convertArticleToResponse);
    }

    /**
     * Get recent articles
     */
    @Transactional(readOnly = true)
    public List<NewsArticleResponse> getRecentArticles(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return newsArticleRepository.findRecentArticles(sinceDate)
                .stream()
                .map(this::convertArticleToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create news article
     */
    public NewsArticleResponse createArticle(NewsArticleRequest request) {
        if (newsArticleRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Article with slug " + request.getSlug() + " already exists");
        }

        NewsArticle article = convertArticleToEntity(request);
        NewsArticle savedArticle = newsArticleRepository.save(article);
        return convertArticleToResponse(savedArticle);
    }

    /**
     * Update news article
     */
    public NewsArticleResponse updateArticle(UUID id, NewsArticleRequest request) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));

        // Check if slug is being changed and if new slug already exists
        if (!article.getSlug().equals(request.getSlug()) && 
            newsArticleRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Article with slug " + request.getSlug() + " already exists");
        }

        updateArticleFromRequest(article, request);
        NewsArticle savedArticle = newsArticleRepository.save(article);
        return convertArticleToResponse(savedArticle);
    }

    /**
     * Delete news article
     */
    public void deleteArticle(UUID id) {
        if (!newsArticleRepository.existsById(id)) {
            throw new IllegalArgumentException("Article not found with id: " + id);
        }
        newsArticleRepository.deleteById(id);
    }

    // News Category methods
    /**
     * Get all active categories
     */
    @Transactional(readOnly = true)
    public List<NewsCategoryResponse> getAllActiveCategories() {
        return newsCategoryRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::convertCategoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get category by slug
     */
    @Transactional(readOnly = true)
    public Optional<NewsCategoryResponse> getCategoryBySlug(String slug) {
        return newsCategoryRepository.findBySlugAndIsActiveTrue(slug)
                .map(this::convertCategoryToResponse);
    }

    /**
     * Create news category
     */
    public NewsCategoryResponse createCategory(NewsCategoryRequest request) {
        if (newsCategoryRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Category with slug " + request.getSlug() + " already exists");
        }

        NewsCategory category = convertCategoryToEntity(request);
        NewsCategory savedCategory = newsCategoryRepository.save(category);
        return convertCategoryToResponse(savedCategory);
    }

    /**
     * Update news category
     */
    public NewsCategoryResponse updateCategory(UUID id, NewsCategoryRequest request) {
        NewsCategory category = newsCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Check if slug is being changed and if new slug already exists
        if (!category.getSlug().equals(request.getSlug()) && 
            newsCategoryRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Category with slug " + request.getSlug() + " already exists");
        }

        updateCategoryFromRequest(category, request);
        NewsCategory savedCategory = newsCategoryRepository.save(category);
        return convertCategoryToResponse(savedCategory);
    }

    /**
     * Delete news category
     */
    public void deleteCategory(UUID id) {
        NewsCategory category = newsCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        category.setIsActive(false);
        newsCategoryRepository.save(category);
    }

    /**
     * Get active announcements (featured articles marked as announcements)
     */
    @Transactional(readOnly = true)
    public List<NewsArticleResponse> getActiveAnnouncements() {
        return newsArticleRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .limit(5)
                .map(this::convertArticleToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get high priority announcements
     */
    @Transactional(readOnly = true)
    public List<NewsArticleResponse> getHighPriorityAnnouncements() {
        LocalDateTime recentDate = LocalDateTime.now().minusDays(7);
        return newsArticleRepository.findRecentArticles(recentDate)
                .stream()
                .filter(article -> article.getIsFeatured())
                .map(this::convertArticleToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Publish article and send notifications
     */
    public NewsArticleResponse publishArticle(UUID id) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));
        
        article.setIsPublished(true);
        if (article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        NewsArticle savedArticle = newsArticleRepository.save(article);
        
        // TODO: Send notifications to subscribers
        // This would integrate with a notification service
        
        return convertArticleToResponse(savedArticle);
    }

    /**
     * Unpublish article
     */
    public NewsArticleResponse unpublishArticle(UUID id) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));
        
        article.setIsPublished(false);
        NewsArticle savedArticle = newsArticleRepository.save(article);
        
        return convertArticleToResponse(savedArticle);
    }

    // Helper methods for NewsArticle
    private NewsArticleResponse convertArticleToResponse(NewsArticle article) {
        NewsArticleResponse response = new NewsArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSlug(article.getSlug());
        response.setSummary(article.getSummary());
        response.setContent(article.getContent());
        response.setFeaturedImageUrl(article.getFeaturedImageUrl());
        response.setAuthor(article.getAuthor());
        response.setPublishedAt(article.getPublishedAt());
        response.setIsPublished(article.getIsPublished());
        response.setIsFeatured(article.getIsFeatured());
        response.setViewCount(article.getViewCount());
        response.setLanguage(article.getLanguage());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        
        if (article.getCategory() != null) {
            response.setCategory(convertCategoryToResponse(article.getCategory()));
        }
        
        return response;
    }

    private NewsArticle convertArticleToEntity(NewsArticleRequest request) {
        NewsArticle article = new NewsArticle();
        updateArticleFromRequest(article, request);
        return article;
    }

    private void updateArticleFromRequest(NewsArticle article, NewsArticleRequest request) {
        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setFeaturedImageUrl(request.getFeaturedImageUrl());
        article.setAuthor(request.getAuthor());
        article.setPublishedAt(request.getPublishedAt());
        article.setIsPublished(request.getIsPublished());
        article.setIsFeatured(request.getIsFeatured());
        article.setLanguage(request.getLanguage());
        
        if (request.getCategoryId() != null) {
            NewsCategory category = newsCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));
            article.setCategory(category);
        }
    }

    // Helper methods for NewsCategory
    private NewsCategoryResponse convertCategoryToResponse(NewsCategory category) {
        NewsCategoryResponse response = new NewsCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setSlug(category.getSlug());
        response.setIsActive(category.getIsActive());
        response.setArticleCount(category.getPublishedArticleCount());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }

    private NewsCategory convertCategoryToEntity(NewsCategoryRequest request) {
        NewsCategory category = new NewsCategory();
        updateCategoryFromRequest(category, request);
        return category;
    }

    private void updateCategoryFromRequest(NewsCategory category, NewsCategoryRequest request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(request.getSlug());
        category.setIsActive(request.getIsActive());
    }
}