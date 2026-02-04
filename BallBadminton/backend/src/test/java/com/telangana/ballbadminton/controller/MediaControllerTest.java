package com.telangana.ballbadminton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telangana.ballbadminton.dto.media.MediaGalleryRequest;
import com.telangana.ballbadminton.dto.media.MediaGalleryResponse;
import com.telangana.ballbadminton.dto.media.MediaItemRequest;
import com.telangana.ballbadminton.dto.media.MediaItemResponse;
import com.telangana.ballbadminton.entity.MediaGallery;
import com.telangana.ballbadminton.entity.MediaItem;
import com.telangana.ballbadminton.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
 * Unit tests for MediaController
 */
@WebMvcTest(MediaController.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MediaGalleryResponse testGalleryResponse;
    private MediaGalleryRequest testGalleryRequest;
    private MediaItemResponse testItemResponse;
    private MediaItemRequest testItemRequest;
    private UUID testGalleryId;
    private UUID testItemId;

    @BeforeEach
    void setUp() {
        testGalleryId = UUID.randomUUID();
        testItemId = UUID.randomUUID();
        
        // Setup gallery
        testGalleryResponse = new MediaGalleryResponse();
        testGalleryResponse.setId(testGalleryId);
        testGalleryResponse.setTitle("Tournament Photos 2024");
        testGalleryResponse.setDescription("Photos from the annual tournament");
        testGalleryResponse.setGalleryType(MediaGallery.GalleryType.PHOTO);
        testGalleryResponse.setCoverImageUrl("/images/cover.jpg");
        testGalleryResponse.setIsFeatured(true);
        testGalleryResponse.setIsPublic(true);
        testGalleryResponse.setMediaItemCount(25L);
        testGalleryResponse.setCreatedAt(LocalDateTime.now());
        testGalleryResponse.setUpdatedAt(LocalDateTime.now());

        testGalleryRequest = new MediaGalleryRequest();
        testGalleryRequest.setTitle("Tournament Photos 2024");
        testGalleryRequest.setDescription("Photos from the annual tournament");
        testGalleryRequest.setGalleryType(MediaGallery.GalleryType.PHOTO);
        testGalleryRequest.setCoverImageUrl("/images/cover.jpg");
        testGalleryRequest.setIsFeatured(true);
        testGalleryRequest.setIsPublic(true);

        // Setup media item
        testItemResponse = new MediaItemResponse();
        testItemResponse.setId(testItemId);
        testItemResponse.setGalleryId(testGalleryId);
        testItemResponse.setTitle("Championship Final");
        testItemResponse.setDescription("Final match of the championship");
        testItemResponse.setFileUrl("/media/championship-final.jpg");
        testItemResponse.setThumbnailUrl("/media/thumbs/championship-final.jpg");
        testItemResponse.setMediaType(MediaItem.MediaType.IMAGE);
        testItemResponse.setFileSize(1024000L);
        testItemResponse.setMimeType("image/jpeg");
        testItemResponse.setSortOrder(1);
        testItemResponse.setIsActive(true);
        testItemResponse.setCreatedAt(LocalDateTime.now());
        testItemResponse.setUpdatedAt(LocalDateTime.now());

        testItemRequest = new MediaItemRequest();
        testItemRequest.setGalleryId(testGalleryId);
        testItemRequest.setTitle("Championship Final");
        testItemRequest.setDescription("Final match of the championship");
        testItemRequest.setFileUrl("/media/championship-final.jpg");
        testItemRequest.setThumbnailUrl("/media/thumbs/championship-final.jpg");
        testItemRequest.setMediaType(MediaItem.MediaType.IMAGE);
        testItemRequest.setFileSize(1024000L);
        testItemRequest.setMimeType("image/jpeg");
        testItemRequest.setSortOrder(1);
        testItemRequest.setIsActive(true);
    }

    // Media Gallery Tests
    @Test
    void getPublicGalleries_ShouldReturnPublicGalleries() throws Exception {
        // Given
        List<MediaGalleryResponse> galleries = Arrays.asList(testGalleryResponse);
        Page<MediaGalleryResponse> galleryPage = new PageImpl<>(galleries);
        when(mediaService.getPublicGalleries(0, 10)).thenReturn(galleryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Tournament Photos 2024"))
                .andExpect(jsonPath("$.content[0].isPublic").value(true));

        verify(mediaService).getPublicGalleries(0, 10);
    }

    @Test
    void getGalleriesByType_ShouldReturnGalleriesByType() throws Exception {
        // Given
        List<MediaGalleryResponse> galleries = Arrays.asList(testGalleryResponse);
        Page<MediaGalleryResponse> galleryPage = new PageImpl<>(galleries);
        when(mediaService.getGalleriesByType(MediaGallery.GalleryType.PHOTO, 0, 10)).thenReturn(galleryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/type/{galleryType}", "PHOTO")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].galleryType").value("PHOTO"));

        verify(mediaService).getGalleriesByType(MediaGallery.GalleryType.PHOTO, 0, 10);
    }

    @Test
    void getFeaturedGalleries_ShouldReturnFeaturedGalleries() throws Exception {
        // Given
        List<MediaGalleryResponse> galleries = Arrays.asList(testGalleryResponse);
        when(mediaService.getFeaturedGalleries()).thenReturn(galleries);

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/featured"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isFeatured").value(true));

        verify(mediaService).getFeaturedGalleries();
    }

    @Test
    void searchGalleries_ShouldReturnMatchingGalleries() throws Exception {
        // Given
        List<MediaGalleryResponse> galleries = Arrays.asList(testGalleryResponse);
        Page<MediaGalleryResponse> galleryPage = new PageImpl<>(galleries);
        when(mediaService.searchGalleries("Tournament", 0, 10)).thenReturn(galleryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/search")
                .param("title", "Tournament")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Tournament Photos 2024"));

        verify(mediaService).searchGalleries("Tournament", 0, 10);
    }

    @Test
    void getGalleryById_WhenGalleryExists_ShouldReturnGallery() throws Exception {
        // Given
        when(mediaService.getGalleryById(testGalleryId)).thenReturn(Optional.of(testGalleryResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/{id}", testGalleryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Tournament Photos 2024"))
                .andExpect(jsonPath("$.mediaItemCount").value(25));

        verify(mediaService).getGalleryById(testGalleryId);
    }

    @Test
    void getGalleryById_WhenGalleryNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(mediaService.getGalleryById(testGalleryId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/{id}", testGalleryId))
                .andExpect(status().isNotFound());

        verify(mediaService).getGalleryById(testGalleryId);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_CREATE")
    void createGallery_WithValidData_ShouldCreateGallery() throws Exception {
        // Given
        when(mediaService.createGallery(any(MediaGalleryRequest.class))).thenReturn(testGalleryResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/media/galleries")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGalleryRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Tournament Photos 2024"))
                .andExpect(jsonPath("$.galleryType").value("PHOTO"));

        verify(mediaService).createGallery(any(MediaGalleryRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_CREATE")
    void createGallery_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        MediaGalleryRequest invalidRequest = new MediaGalleryRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/media/galleries")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(mediaService, never()).createGallery(any(MediaGalleryRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_UPDATE")
    void updateGallery_WithValidData_ShouldUpdateGallery() throws Exception {
        // Given
        when(mediaService.updateGallery(eq(testGalleryId), any(MediaGalleryRequest.class)))
                .thenReturn(testGalleryResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/media/galleries/{id}", testGalleryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGalleryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Tournament Photos 2024"));

        verify(mediaService).updateGallery(eq(testGalleryId), any(MediaGalleryRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_DELETE")
    void deleteGallery_ShouldDeleteGallery() throws Exception {
        // Given
        doNothing().when(mediaService).deleteGallery(testGalleryId);

        // When & Then
        mockMvc.perform(delete("/api/v1/media/galleries/{id}", testGalleryId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(mediaService).deleteGallery(testGalleryId);
    }

    // Media Item Tests
    @Test
    void getMediaItemsByGallery_ShouldReturnMediaItems() throws Exception {
        // Given
        List<MediaItemResponse> items = Arrays.asList(testItemResponse);
        when(mediaService.getMediaItemsByGallery(testGalleryId)).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/v1/media/galleries/{galleryId}/items", testGalleryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Championship Final"))
                .andExpect(jsonPath("$[0].galleryId").value(testGalleryId.toString()));

        verify(mediaService).getMediaItemsByGallery(testGalleryId);
    }

    @Test
    void getMediaItemsByType_ShouldReturnMediaItemsByType() throws Exception {
        // Given
        List<MediaItemResponse> items = Arrays.asList(testItemResponse);
        Page<MediaItemResponse> itemPage = new PageImpl<>(items);
        when(mediaService.getMediaItemsByType(MediaItem.MediaType.IMAGE, 0, 10)).thenReturn(itemPage);

        // When & Then
        mockMvc.perform(get("/api/v1/media/items/type/{mediaType}", "IMAGE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].mediaType").value("IMAGE"));

        verify(mediaService).getMediaItemsByType(MediaItem.MediaType.IMAGE, 0, 10);
    }

    @Test
    void getMediaItemById_WhenItemExists_ShouldReturnItem() throws Exception {
        // Given
        when(mediaService.getMediaItemById(testItemId)).thenReturn(Optional.of(testItemResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/media/items/{id}", testItemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Championship Final"))
                .andExpect(jsonPath("$.mediaType").value("IMAGE"));

        verify(mediaService).getMediaItemById(testItemId);
    }

    @Test
    void getMediaItemById_WhenItemNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(mediaService.getMediaItemById(testItemId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/media/items/{id}", testItemId))
                .andExpect(status().isNotFound());

        verify(mediaService).getMediaItemById(testItemId);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_CREATE")
    void createMediaItem_WithValidData_ShouldCreateItem() throws Exception {
        // Given
        when(mediaService.createMediaItem(any(MediaItemRequest.class))).thenReturn(testItemResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/media/items")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Championship Final"))
                .andExpect(jsonPath("$.mediaType").value("IMAGE"));

        verify(mediaService).createMediaItem(any(MediaItemRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_CREATE")
    void uploadMediaFile_WithValidFile_ShouldUploadFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());
        
        when(mediaService.uploadMediaFile(eq(testGalleryId), any(), eq("Test Image"), eq("Test description")))
                .thenReturn(testItemResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/media/galleries/{galleryId}/upload", testGalleryId)
                .file(file)
                .param("title", "Test Image")
                .param("description", "Test description")
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Championship Final"));

        verify(mediaService).uploadMediaFile(eq(testGalleryId), any(), eq("Test Image"), eq("Test description"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_UPDATE")
    void updateMediaItem_WithValidData_ShouldUpdateItem() throws Exception {
        // Given
        when(mediaService.updateMediaItem(eq(testItemId), any(MediaItemRequest.class)))
                .thenReturn(testItemResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/media/items/{id}", testItemId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testItemRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Championship Final"));

        verify(mediaService).updateMediaItem(eq(testItemId), any(MediaItemRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_DELETE")
    void deleteMediaItem_ShouldDeleteItem() throws Exception {
        // Given
        doNothing().when(mediaService).deleteMediaItem(testItemId);

        // When & Then
        mockMvc.perform(delete("/api/v1/media/items/{id}", testItemId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(mediaService).deleteMediaItem(testItemId);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEDIA_READ")
    void getMediaStatistics_ShouldReturnStatistics() throws Exception {
        // Given
        MediaService.MediaStatistics stats = new MediaService.MediaStatistics(10L, 8L, 2L, 150L, 25L);
        when(mediaService.getMediaStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/v1/media/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalGalleries").value(10))
                .andExpect(jsonPath("$.photoGalleries").value(8))
                .andExpect(jsonPath("$.videoGalleries").value(2))
                .andExpect(jsonPath("$.totalImages").value(150))
                .andExpect(jsonPath("$.totalVideos").value(25));

        verify(mediaService).getMediaStatistics();
    }

    // Permission Tests
    @Test
    void createGallery_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/media/galleries")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGalleryRequest)))
                .andExpect(status().isForbidden());

        verify(mediaService, never()).createGallery(any(MediaGalleryRequest.class));
    }

    @Test
    void updateGallery_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/media/galleries/{id}", testGalleryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGalleryRequest)))
                .andExpect(status().isForbidden());

        verify(mediaService, never()).updateGallery(any(UUID.class), any(MediaGalleryRequest.class));
    }

    @Test
    void deleteGallery_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/media/galleries/{id}", testGalleryId)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(mediaService, never()).deleteGallery(any(UUID.class));
    }

    @Test
    void uploadMediaFile_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        // When & Then
        mockMvc.perform(multipart("/api/v1/media/galleries/{galleryId}/upload", testGalleryId)
                .file(file)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(mediaService, never()).uploadMediaFile(any(UUID.class), any(), any(), any());
    }
}