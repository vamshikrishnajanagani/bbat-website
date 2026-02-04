package com.telangana.ballbadminton.e2e;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.dto.member.CreateMemberRequest;
import com.telangana.ballbadminton.dto.member.UpdateMemberRequest;
import com.telangana.ballbadminton.entity.Member;
import com.telangana.ballbadminton.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End tests for Member Management functionality
 * 
 * Tests critical user journeys:
 * - Creating a new member
 * - Retrieving member details
 * - Updating member information
 * - Listing members with hierarchy
 * - Contact form routing
 * 
 * Validates Requirements: 1.1, 1.2, 1.3, 1.4, 1.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("Member Management E2E Tests")
class MemberManagementE2ETest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void cleanDatabase() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Complete member lifecycle - create, read, update, delete")
    void testCompleteMemberLifecycle() throws Exception {
        // Step 1: Create a new member
        CreateMemberRequest createRequest = new CreateMemberRequest();
        createRequest.setName("Dr. Rajesh Kumar");
        createRequest.setPosition("President");
        createRequest.setEmail("president@telanganaballbadminton.org");
        createRequest.setPhone("+91-9876543210");
        createRequest.setBiography("Experienced sports administrator with 20 years in Ball Badminton");
        createRequest.setHierarchy(1);
        createRequest.setTenureStartDate(LocalDate.of(2023, 1, 1));

        MvcResult createResult = mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dr. Rajesh Kumar"))
                .andExpect(jsonPath("$.position").value("President"))
                .andExpect(jsonPath("$.email").value("president@telanganaballbadminton.org"))
                .andExpect(jsonPath("$.hierarchy").value(1))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String memberId = objectMapper.readTree(responseBody).get("id").asText();

        // Step 2: Verify member is stored in database
        Member savedMember = memberRepository.findById(java.util.UUID.fromString(memberId)).orElse(null);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("Dr. Rajesh Kumar");
        assertThat(savedMember.getPosition()).isEqualTo("President");

        // Step 3: Retrieve member via API
        mockMvc.perform(get("/api/v1/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId))
                .andExpect(jsonPath("$.name").value("Dr. Rajesh Kumar"))
                .andExpect(jsonPath("$.position").value("President"))
                .andExpect(jsonPath("$.email").value("president@telanganaballbadminton.org"))
                .andExpect(jsonPath("$.biography").value("Experienced sports administrator with 20 years in Ball Badminton"));

        // Step 4: Update member information
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setPhone("+91-9876543211");
        updateRequest.setBiography("Experienced sports administrator with 25 years in Ball Badminton");

        mockMvc.perform(put("/api/v1/members/" + memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+91-9876543211"))
                .andExpect(jsonPath("$.biography").value("Experienced sports administrator with 25 years in Ball Badminton"));

        // Step 5: Verify update is reflected immediately (Real-Time Update Propagation)
        mockMvc.perform(get("/api/v1/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+91-9876543211"))
                .andExpect(jsonPath("$.biography").value("Experienced sports administrator with 25 years in Ball Badminton"));

        // Step 6: Delete member
        mockMvc.perform(delete("/api/v1/members/" + memberId))
                .andExpect(status().isNoContent());

        // Step 7: Verify member is deleted
        mockMvc.perform(get("/api/v1/members/" + memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("E2E: Member hierarchy ordering is preserved")
    void testMemberHierarchyOrdering() throws Exception {
        // Create multiple members with different hierarchy levels
        createMember("President", 1);
        createMember("Vice President", 2);
        createMember("Secretary", 3);
        createMember("Treasurer", 4);
        createMember("Joint Secretary", 5);

        // Retrieve all members and verify ordering
        MvcResult result = mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        var members = objectMapper.readTree(responseBody).get("content");

        // Verify hierarchy ordering (lower numbers first)
        assertThat(members.get(0).get("position").asText()).isEqualTo("President");
        assertThat(members.get(0).get("hierarchy").asInt()).isEqualTo(1);
        assertThat(members.get(1).get("position").asText()).isEqualTo("Vice President");
        assertThat(members.get(1).get("hierarchy").asInt()).isEqualTo(2);
        assertThat(members.get(4).get("position").asText()).isEqualTo("Joint Secretary");
        assertThat(members.get(4).get("hierarchy").asInt()).isEqualTo(5);
    }

    @Test
    @DisplayName("E2E: Contact form routing to correct member")
    void testContactFormRouting() throws Exception {
        // Create members with different roles
        MvcResult presidentResult = createMember("President", 1);
        String presidentId = extractIdFromResponse(presidentResult);

        MvcResult secretaryResult = createMember("Secretary", 3);
        String secretaryId = extractIdFromResponse(secretaryResult);

        // Test contact form routing to President
        mockMvc.perform(post("/api/v1/members/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"President\",\"name\":\"John Doe\",\"email\":\"john@example.com\",\"message\":\"Test message\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientId").value(presidentId))
                .andExpect(jsonPath("$.recipientPosition").value("President"));

        // Test contact form routing to Secretary
        mockMvc.perform(post("/api/v1/members/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"Secretary\",\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"message\":\"Another test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientId").value(secretaryId))
                .andExpect(jsonPath("$.recipientPosition").value("Secretary"));
    }

    @Test
    @DisplayName("E2E: Data consistency across multiple operations")
    void testDataConsistencyAcrossOperations() throws Exception {
        // Create a member
        MvcResult createResult = createMember("President", 1);
        String memberId = extractIdFromResponse(createResult);

        // Perform multiple read operations and verify consistency
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/members/" + memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.position").value("President"))
                    .andExpect(jsonPath("$.hierarchy").value(1));
        }

        // Update and verify consistency
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setPhone("+91-1234567890");

        mockMvc.perform(put("/api/v1/members/" + memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isOk());

        // Verify update is immediately reflected in all subsequent reads
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/members/" + memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phone").value("+91-1234567890"));
        }
    }

    @Test
    @DisplayName("E2E: Required fields validation")
    void testRequiredFieldsValidation() throws Exception {
        // Test missing required fields
        CreateMemberRequest invalidRequest = new CreateMemberRequest();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setPosition("President");

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        // Test invalid email format
        CreateMemberRequest invalidEmailRequest = new CreateMemberRequest();
        invalidEmailRequest.setName("Test Member");
        invalidEmailRequest.setPosition("Secretary");
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setHierarchy(3);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // Helper methods
    private MvcResult createMember(String position, int hierarchy) throws Exception {
        CreateMemberRequest request = new CreateMemberRequest();
        request.setName("Test " + position);
        request.setPosition(position);
        request.setEmail(position.toLowerCase().replace(" ", "") + "@test.com");
        request.setPhone("+91-98765432" + hierarchy + "0");
        request.setBiography("Test biography for " + position);
        request.setHierarchy(hierarchy);
        request.setTenureStartDate(LocalDate.now());

        return mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String extractIdFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asText();
    }
}
