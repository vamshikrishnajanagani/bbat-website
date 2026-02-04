package com.telangana.ballbadminton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telangana.ballbadminton.dto.district.DistrictRequest;
import com.telangana.ballbadminton.dto.district.DistrictResponse;
import com.telangana.ballbadminton.service.DistrictService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for DistrictController
 */
@WebMvcTest(controllers = DistrictController.class, 
           excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class DistrictControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DistrictService districtService;

    @Autowired
    private ObjectMapper objectMapper;

    private DistrictResponse testDistrictResponse;
    private DistrictRequest testDistrictRequest;
    private UUID testDistrictId;

    @BeforeEach
    void setUp() {
        testDistrictId = UUID.randomUUID();
        
        testDistrictResponse = new DistrictResponse();
        testDistrictResponse.setId(testDistrictId);
        testDistrictResponse.setName("Hyderabad");
        testDistrictResponse.setCode("HYD");
        testDistrictResponse.setHeadquarters("Hyderabad");
        testDistrictResponse.setAreaSqKm(new BigDecimal("217.00"));
        testDistrictResponse.setPopulation(10000000L);
        testDistrictResponse.setLatitude(new BigDecimal("17.3850"));
        testDistrictResponse.setLongitude(new BigDecimal("78.4867"));
        testDistrictResponse.setContactPerson("John Doe");
        testDistrictResponse.setContactEmail("john@example.com");
        testDistrictResponse.setContactPhone("+91-9876543210");
        testDistrictResponse.setDescription("Capital district of Telangana");
        testDistrictResponse.setIsActive(true);
        testDistrictResponse.setPlayerCount(150L);
        testDistrictResponse.setTournamentCount(25L);
        testDistrictResponse.setCreatedAt(LocalDateTime.now());
        testDistrictResponse.setUpdatedAt(LocalDateTime.now());

        testDistrictRequest = new DistrictRequest();
        testDistrictRequest.setName("Hyderabad");
        testDistrictRequest.setCode("HYD");
        testDistrictRequest.setHeadquarters("Hyderabad");
        testDistrictRequest.setAreaSqKm(new BigDecimal("217.00"));
        testDistrictRequest.setPopulation(10000000L);
        testDistrictRequest.setLatitude(new BigDecimal("17.3850"));
        testDistrictRequest.setLongitude(new BigDecimal("78.4867"));
        testDistrictRequest.setContactPerson("John Doe");
        testDistrictRequest.setContactEmail("john@example.com");
        testDistrictRequest.setContactPhone("+91-9876543210");
        testDistrictRequest.setDescription("Capital district of Telangana");
        testDistrictRequest.setIsActive(true);
    }

    @Test
    void getAllActiveDistricts_ShouldReturnAllActiveDistricts() throws Exception {
        // Given
        List<DistrictResponse> districts = Arrays.asList(testDistrictResponse);
        when(districtService.getAllActiveDistricts()).thenReturn(districts);

        // When & Then
        mockMvc.perform(get("/api/v1/districts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Hyderabad"))
                .andExpect(jsonPath("$[0].code").value("HYD"));

        verify(districtService).getAllActiveDistricts();
    }

    @Test
    void getDistricts_WithPagination_ShouldReturnPagedResults() throws Exception {
        // Given
        List<DistrictResponse> districts = Arrays.asList(testDistrictResponse);
        Page<DistrictResponse> districtPage = new PageImpl<>(districts);
        when(districtService.getDistricts(0, 10, "name", "asc")).thenReturn(districtPage);

        // When & Then
        mockMvc.perform(get("/api/v1/districts/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Hyderabad"));

        verify(districtService).getDistricts(0, 10, "name", "asc");
    }

    @Test
    void getDistrictById_WhenDistrictExists_ShouldReturnDistrict() throws Exception {
        // Given
        when(districtService.getDistrictById(testDistrictId)).thenReturn(Optional.of(testDistrictResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/districts/{id}", testDistrictId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Hyderabad"))
                .andExpect(jsonPath("$.code").value("HYD"));

        verify(districtService).getDistrictById(testDistrictId);
    }

    @Test
    void getDistrictById_WhenDistrictNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(districtService.getDistrictById(testDistrictId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/districts/{id}", testDistrictId))
                .andExpect(status().isNotFound());

        verify(districtService).getDistrictById(testDistrictId);
    }

    @Test
    void getDistrictByCode_WhenDistrictExists_ShouldReturnDistrict() throws Exception {
        // Given
        when(districtService.getDistrictByCode("HYD")).thenReturn(Optional.of(testDistrictResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/districts/code/{code}", "HYD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Hyderabad"))
                .andExpect(jsonPath("$.code").value("HYD"));

        verify(districtService).getDistrictByCode("HYD");
    }

    @Test
    void searchDistricts_ShouldReturnMatchingDistricts() throws Exception {
        // Given
        List<DistrictResponse> districts = Arrays.asList(testDistrictResponse);
        Page<DistrictResponse> districtPage = new PageImpl<>(districts);
        when(districtService.searchDistricts("Hyderabad", 0, 10)).thenReturn(districtPage);

        // When & Then
        mockMvc.perform(get("/api/v1/districts/search")
                .param("name", "Hyderabad")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Hyderabad"));

        verify(districtService).searchDistricts("Hyderabad", 0, 10);
    }

    @Test
    void getDistrictsWithStatistics_ShouldReturnDistrictsWithStats() throws Exception {
        // Given
        List<DistrictResponse> districts = Arrays.asList(testDistrictResponse);
        when(districtService.getDistrictsWithStatistics()).thenReturn(districts);

        // When & Then
        mockMvc.perform(get("/api/v1/districts/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].playerCount").value(150))
                .andExpect(jsonPath("$[0].tournamentCount").value(25));

        verify(districtService).getDistrictsWithStatistics();
    }

    @Test
    void createDistrict_WithValidData_ShouldCreateDistrict() throws Exception {
        // Given
        when(districtService.createDistrict(any(DistrictRequest.class))).thenReturn(testDistrictResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/districts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDistrictRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Hyderabad"))
                .andExpect(jsonPath("$.code").value("HYD"));

        verify(districtService).createDistrict(any(DistrictRequest.class));
    }

    @Test
    void createDistrict_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        DistrictRequest invalidRequest = new DistrictRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/districts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(districtService, never()).createDistrict(any(DistrictRequest.class));
    }

    @Test
    void updateDistrict_WithValidData_ShouldUpdateDistrict() throws Exception {
        // Given
        when(districtService.updateDistrict(eq(testDistrictId), any(DistrictRequest.class)))
                .thenReturn(testDistrictResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/districts/{id}", testDistrictId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDistrictRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Hyderabad"));

        verify(districtService).updateDistrict(eq(testDistrictId), any(DistrictRequest.class));
    }

    @Test
    void deleteDistrict_ShouldDeleteDistrict() throws Exception {
        // Given
        doNothing().when(districtService).deleteDistrict(testDistrictId);

        // When & Then
        mockMvc.perform(delete("/api/v1/districts/{id}", testDistrictId))
                .andExpect(status().isNoContent());

        verify(districtService).deleteDistrict(testDistrictId);
    }

    @Test
    void getDistrictStatistics_ShouldReturnStatistics() throws Exception {
        // Given
        DistrictService.DistrictStatistics stats = new DistrictService.DistrictStatistics(33L, 5000L, 200L);
        when(districtService.getDistrictStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/v1/districts/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalDistricts").value(33))
                .andExpect(jsonPath("$.totalPlayers").value(5000))
                .andExpect(jsonPath("$.totalTournaments").value(200));

        verify(districtService).getDistrictStatistics();
    }

    @Test
    void createDistrict_WithDuplicateCode_ShouldReturnBadRequest() throws Exception {
        // Given
        when(districtService.createDistrict(any(DistrictRequest.class)))
                .thenThrow(new IllegalArgumentException("District with code HYD already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/districts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDistrictRequest)))
                .andExpect(status().isBadRequest());

        verify(districtService).createDistrict(any(DistrictRequest.class));
    }
}