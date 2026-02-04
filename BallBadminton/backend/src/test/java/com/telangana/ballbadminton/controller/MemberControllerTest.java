package com.telangana.ballbadminton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telangana.ballbadminton.dto.member.ContactFormRequest;
import com.telangana.ballbadminton.dto.member.MemberRequest;
import com.telangana.ballbadminton.dto.member.MemberResponse;
import com.telangana.ballbadminton.service.FileUploadService;
import com.telangana.ballbadminton.service.MemberService;
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

import java.time.LocalDate;
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
 * Unit tests for MemberController
 */
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private FileUploadService fileUploadService;

    @Autowired
    private ObjectMapper objectMapper;

    private MemberResponse testMemberResponse;
    private MemberRequest testMemberRequest;
    private UUID testMemberId;

    @BeforeEach
    void setUp() {
        testMemberId = UUID.randomUUID();
        
        testMemberResponse = new MemberResponse();
        testMemberResponse.setId(testMemberId);
        testMemberResponse.setName("John Doe");
        testMemberResponse.setPosition("President");
        testMemberResponse.setEmail("john.doe@example.com");
        testMemberResponse.setPhone("+91-9876543210");
        testMemberResponse.setBiography("Experienced sports administrator");
        testMemberResponse.setHierarchyLevel(1);
        testMemberResponse.setTenureStartDate(LocalDate.of(2023, 1, 1));
        testMemberResponse.setTenureEndDate(LocalDate.now().plusYears(1));
        testMemberResponse.setIsActive(true);
        testMemberResponse.setIsProminent(true);
        testMemberResponse.setIsCurrentlyServing(true);
        testMemberResponse.setHasTenureExpired(false);
        testMemberResponse.setCreatedAt(LocalDateTime.now());
        testMemberResponse.setUpdatedAt(LocalDateTime.now());

        testMemberRequest = new MemberRequest();
        testMemberRequest.setName("John Doe");
        testMemberRequest.setPosition("President");
        testMemberRequest.setEmail("john.doe@example.com");
        testMemberRequest.setPhone("+91-9876543210");
        testMemberRequest.setBiography("Experienced sports administrator");
        testMemberRequest.setHierarchyLevel(1);
        testMemberRequest.setTenureStartDate(LocalDate.of(2023, 1, 1));
        testMemberRequest.setTenureEndDate(LocalDate.now().plusYears(1));
        testMemberRequest.setIsActive(true);
        testMemberRequest.setIsProminent(true);
    }

    @Test
    void getAllActiveMembers_ShouldReturnAllActiveMembers() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        when(memberService.getAllActiveMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].position").value("President"));

        verify(memberService).getAllActiveMembers();
    }

    @Test
    void getActiveMembers_WithPagination_ShouldReturnPagedResults() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        Page<MemberResponse> memberPage = new PageImpl<>(members);
        when(memberService.getActiveMembers(0, 10, "hierarchyLevel", "asc")).thenReturn(memberPage);

        // When & Then
        mockMvc.perform(get("/api/v1/members/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "hierarchyLevel")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));

        verify(memberService).getActiveMembers(0, 10, "hierarchyLevel", "asc");
    }

    @Test
    void getMemberById_WhenMemberExists_ShouldReturnMember() throws Exception {
        // Given
        when(memberService.getMemberById(testMemberId)).thenReturn(Optional.of(testMemberResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/members/{id}", testMemberId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.position").value("President"));

        verify(memberService).getMemberById(testMemberId);
    }

    @Test
    void getMemberById_WhenMemberNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(memberService.getMemberById(testMemberId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/members/{id}", testMemberId))
                .andExpect(status().isNotFound());

        verify(memberService).getMemberById(testMemberId);
    }

    @Test
    void getProminentMembers_ShouldReturnProminentMembers() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        when(memberService.getProminentMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/v1/members/prominent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isProminent").value(true));

        verify(memberService).getProminentMembers();
    }

    @Test
    void getCurrentlyServingMembers_ShouldReturnCurrentlyServingMembers() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        when(memberService.getCurrentlyServingMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/v1/members/currently-serving"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isCurrentlyServing").value(true));

        verify(memberService).getCurrentlyServingMembers();
    }

    @Test
    void searchMembers_ShouldReturnMatchingMembers() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        Page<MemberResponse> memberPage = new PageImpl<>(members);
        when(memberService.searchMembers("John", 0, 10)).thenReturn(memberPage);

        // When & Then
        mockMvc.perform(get("/api/v1/members/search")
                .param("q", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));

        verify(memberService).searchMembers("John", 0, 10);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_CREATE")
    void createMember_WithValidData_ShouldCreateMember() throws Exception {
        // Given
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(testMemberResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMemberRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.position").value("President"));

        verify(memberService).createMember(any(MemberRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_CREATE")
    void createMember_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        MemberRequest invalidRequest = new MemberRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).createMember(any(MemberRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_UPDATE")
    void updateMember_WithValidData_ShouldUpdateMember() throws Exception {
        // Given
        when(memberService.updateMember(eq(testMemberId), any(MemberRequest.class)))
                .thenReturn(testMemberResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/members/{id}", testMemberId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMemberRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService).updateMember(eq(testMemberId), any(MemberRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_DELETE")
    void deleteMember_ShouldDeleteMember() throws Exception {
        // Given
        doNothing().when(memberService).deleteMember(testMemberId);

        // When & Then
        mockMvc.perform(delete("/api/v1/members/{id}", testMemberId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember(testMemberId);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_UPDATE")
    void uploadMemberPhoto_WithValidFile_ShouldUploadPhoto() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test image content".getBytes());
        String photoUrl = "/uploads/members/photo.jpg";
        
        when(fileUploadService.uploadMemberPhoto(any())).thenReturn(photoUrl);
        when(memberService.updateMemberPhoto(testMemberId, photoUrl)).thenReturn(testMemberResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/members/{id}/photo", testMemberId)
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(fileUploadService).uploadMemberPhoto(any());
        verify(memberService).updateMemberPhoto(testMemberId, photoUrl);
    }

    @Test
    void submitContactForm_WithValidData_ShouldSubmitForm() throws Exception {
        // Given
        ContactFormRequest contactRequest = new ContactFormRequest();
        contactRequest.setMemberId(testMemberId);
        contactRequest.setSenderName("Jane Smith");
        contactRequest.setSenderEmail("jane@example.com");
        contactRequest.setSubject("Test Subject");
        contactRequest.setMessage("Test Message");

        doNothing().when(memberService).handleContactForm(any(ContactFormRequest.class));

        // When & Then
        mockMvc.perform(post("/api/v1/members/contact")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Contact form submitted successfully"));

        verify(memberService).handleContactForm(any(ContactFormRequest.class));
    }

    @Test
    void submitContactForm_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ContactFormRequest invalidRequest = new ContactFormRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/members/contact")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).handleContactForm(any(ContactFormRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_READ")
    void getMemberStatistics_ShouldReturnStatistics() throws Exception {
        // Given
        MemberService.MemberStatistics stats = new MemberService.MemberStatistics(10L, 5L, 8L);
        when(memberService.getMemberStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/v1/members/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalActive").value(10))
                .andExpect(jsonPath("$.prominent").value(5))
                .andExpect(jsonPath("$.currentlyServing").value(8));

        verify(memberService).getMemberStatistics();
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_MEMBER_READ")
    void getMembersWithTenureEndingSoon_ShouldReturnMembers() throws Exception {
        // Given
        List<MemberResponse> members = Arrays.asList(testMemberResponse);
        when(memberService.getMembersWithTenureEndingSoon()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/v1/members/tenure-ending-soon"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(memberService).getMembersWithTenureEndingSoon();
    }

    @Test
    void createMember_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMemberRequest)))
                .andExpect(status().isForbidden());

        verify(memberService, never()).createMember(any(MemberRequest.class));
    }

    @Test
    void updateMember_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/members/{id}", testMemberId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMemberRequest)))
                .andExpect(status().isForbidden());

        verify(memberService, never()).updateMember(any(UUID.class), any(MemberRequest.class));
    }

    @Test
    void deleteMember_WithoutPermission_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/members/{id}", testMemberId)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(memberService, never()).deleteMember(any(UUID.class));
    }
}