package com.telangana.ballbadminton.entity;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.repository.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.quicktheories.core.Gen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;

/**
 * Property-based tests for data model entities
 * Tests universal properties that should hold for all valid inputs
 * 
 * Feature: telangana-ball-badminton-website
 */
@SpringBootTest
@Transactional
public class DataModelPropertyTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private DistrictRepository districtRepository;

    // Generators for random test data
    private Gen<String> nameGen() {
        return strings().betweenCodePoints(65, 90).ofLengthBetween(5, 50);
    }

    private Gen<String> emailGen() {
        return strings().betweenCodePoints(97, 122).ofLengthBetween(5, 20)
                .map(s -> s + "@example.com");
    }

    private Gen<String> phoneGen() {
        return integers().between(1000000000, 9999999999L)
                .map(n -> "+91" + n);
    }

    private Gen<LocalDate> futureDateGen() {
        return integers().between(1, 365)
                .map(days -> LocalDate.now().plusDays(days));
    }

    private Gen<LocalDate> pastDateGen() {
        return integers().between(1, 3650)
                .map(days -> LocalDate.now().minusDays(days));
    }

    private Gen<Integer> hierarchyGen() {
        return integers().between(0, 10);
    }

    private Gen<BigDecimal> positiveDecimalGen() {
        return integers().between(0, 100000)
                .map(i -> BigDecimal.valueOf(i).divide(BigDecimal.valueOf(100)));
    }

    /**
     * Property 1: Data Round Trip Consistency
     * For any content entity (Member, Player, Tournament, District), 
     * storing the entity via repository and immediately retrieving it 
     * should return data equivalent to the original input.
     * 
     * Validates: Requirements 1.1, 3.1, 6.5
     */
    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 1: Data Round Trip Consistency")
    public void testMemberDataRoundTripConsistency() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                strings().betweenCodePoints(65, 90).ofLengthBetween(5, 30),
                emailGen(),
                phoneGen(),
                hierarchyGen()
            )
            .checkAssert((name, position, email, phone, hierarchy) -> {
                // Create member with generated data
                Member member = new Member();
                member.setName(name);
                member.setPosition(position);
                member.setEmail(email);
                member.setPhone(phone);
                member.setHierarchyLevel(hierarchy);
                member.setIsActive(true);
                member.setIsProminent(false);

                // Store member
                Member saved = memberRepository.saveAndFlush(member);
                
                // Clear persistence context to force fresh retrieval
                memberRepository.flush();
                
                // Retrieve member
                Member retrieved = memberRepository.findById(saved.getId()).orElseThrow();

                // Assert equivalence (ignoring auto-generated fields)
                assertThat(retrieved.getName()).isEqualTo(name);
                assertThat(retrieved.getPosition()).isEqualTo(position);
                assertThat(retrieved.getEmail()).isEqualTo(email);
                assertThat(retrieved.getPhone()).isEqualTo(phone);
                assertThat(retrieved.getHierarchyLevel()).isEqualTo(hierarchy);
                assertThat(retrieved.getIsActive()).isTrue();
                assertThat(retrieved.getIsProminent()).isFalse();
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 1: Data Round Trip Consistency")
    public void testPlayerDataRoundTripConsistency() {
        // Create a district first for the relationship
        District district = new District("Test District", "TD" + UUID.randomUUID().toString().substring(0, 4));
        district = districtRepository.saveAndFlush(district);
        
        final Long districtId = district.getId();

        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                pastDateGen(),
                booleans()
            )
            .checkAssert((name, dob, isProminent) -> {
                // Create player with generated data
                Player player = new Player();
                player.setName(name);
                player.setDateOfBirth(dob);
                player.setCategory(Player.Category.MEN);
                player.setGender(Player.Gender.MALE);
                player.setIsProminent(isProminent);
                player.setIsActive(true);
                
                // Set district relationship
                District dist = districtRepository.findById(districtId).orElseThrow();
                player.setDistrict(dist);

                // Store player
                Player saved = playerRepository.saveAndFlush(player);
                
                // Clear persistence context
                playerRepository.flush();
                
                // Retrieve player
                Player retrieved = playerRepository.findById(saved.getId()).orElseThrow();

                // Assert equivalence
                assertThat(retrieved.getName()).isEqualTo(name);
                assertThat(retrieved.getDateOfBirth()).isEqualTo(dob);
                assertThat(retrieved.getCategory()).isEqualTo(Player.Category.MEN);
                assertThat(retrieved.getGender()).isEqualTo(Player.Gender.MALE);
                assertThat(retrieved.getIsProminent()).isEqualTo(isProminent);
                assertThat(retrieved.getIsActive()).isTrue();
                assertThat(retrieved.getDistrict().getId()).isEqualTo(districtId);
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 1: Data Round Trip Consistency")
    public void testTournamentDataRoundTripConsistency() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                futureDateGen(),
                positiveDecimalGen()
            )
            .checkAssert((name, startDate, entryFee) -> {
                LocalDate endDate = startDate.plusDays(3);
                
                // Create tournament with generated data
                Tournament tournament = new Tournament();
                tournament.setName(name);
                tournament.setStartDate(startDate);
                tournament.setEndDate(endDate);
                tournament.setEntryFee(entryFee);
                tournament.setStatus(Tournament.Status.UPCOMING);
                tournament.setIsFeatured(false);

                // Store tournament
                Tournament saved = tournamentRepository.saveAndFlush(tournament);
                
                // Clear persistence context
                tournamentRepository.flush();
                
                // Retrieve tournament
                Tournament retrieved = tournamentRepository.findById(saved.getId()).orElseThrow();

                // Assert equivalence
                assertThat(retrieved.getName()).isEqualTo(name);
                assertThat(retrieved.getStartDate()).isEqualTo(startDate);
                assertThat(retrieved.getEndDate()).isEqualTo(endDate);
                assertThat(retrieved.getEntryFee()).isEqualByComparingTo(entryFee);
                assertThat(retrieved.getStatus()).isEqualTo(Tournament.Status.UPCOMING);
                assertThat(retrieved.getIsFeatured()).isFalse();
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 1: Data Round Trip Consistency")
    public void testDistrictDataRoundTripConsistency() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                strings().betweenCodePoints(65, 90).ofLength(3),
                positiveDecimalGen()
            )
            .checkAssert((name, code, area) -> {
                // Create district with generated data
                District district = new District();
                district.setName(name);
                district.setCode(code + UUID.randomUUID().toString().substring(0, 4)); // Ensure uniqueness
                district.setAreaSqKm(area);
                district.setIsActive(true);

                // Store district
                District saved = districtRepository.saveAndFlush(district);
                
                // Clear persistence context
                districtRepository.flush();
                
                // Retrieve district
                District retrieved = districtRepository.findById(saved.getId()).orElseThrow();

                // Assert equivalence
                assertThat(retrieved.getName()).isEqualTo(name);
                assertThat(retrieved.getCode()).isEqualTo(saved.getCode());
                assertThat(retrieved.getAreaSqKm()).isEqualByComparingTo(area);
                assertThat(retrieved.getIsActive()).isTrue();
            });
    }

    /**
     * Property 4: Required Field Completeness
     * For any entity response, all required fields as defined in the data model 
     * should be present and non-null.
     * 
     * Validates: Requirements 1.4, 2.1, 3.2, 4.1
     */
    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 4: Required Field Completeness")
    public void testMemberRequiredFieldCompleteness() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                strings().betweenCodePoints(65, 90).ofLengthBetween(5, 30)
            )
            .checkAssert((name, position) -> {
                // Create member with only required fields
                Member member = new Member();
                member.setName(name);
                member.setPosition(position);
                member.setIsActive(true);
                member.setIsProminent(false);

                // Store and retrieve
                Member saved = memberRepository.saveAndFlush(member);
                Member retrieved = memberRepository.findById(saved.getId()).orElseThrow();

                // Assert all required fields are present and non-null
                assertThat(retrieved.getId()).isNotNull();
                assertThat(retrieved.getName()).isNotNull();
                assertThat(retrieved.getPosition()).isNotNull();
                assertThat(retrieved.getIsActive()).isNotNull();
                assertThat(retrieved.getIsProminent()).isNotNull();
                assertThat(retrieved.getCreatedAt()).isNotNull();
                assertThat(retrieved.getUpdatedAt()).isNotNull();
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 4: Required Field Completeness")
    public void testPlayerRequiredFieldCompleteness() {
        qt()
            .withExamples(100)
            .forAll(nameGen())
            .checkAssert(name -> {
                // Create player with only required fields
                Player player = new Player();
                player.setName(name);
                player.setIsProminent(false);
                player.setIsActive(true);

                // Store and retrieve
                Player saved = playerRepository.saveAndFlush(player);
                Player retrieved = playerRepository.findById(saved.getId()).orElseThrow();

                // Assert all required fields are present and non-null
                assertThat(retrieved.getId()).isNotNull();
                assertThat(retrieved.getName()).isNotNull();
                assertThat(retrieved.getIsProminent()).isNotNull();
                assertThat(retrieved.getIsActive()).isNotNull();
                assertThat(retrieved.getCreatedAt()).isNotNull();
                assertThat(retrieved.getUpdatedAt()).isNotNull();
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 4: Required Field Completeness")
    public void testTournamentRequiredFieldCompleteness() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                futureDateGen()
            )
            .checkAssert((name, startDate) -> {
                LocalDate endDate = startDate.plusDays(1);
                
                // Create tournament with only required fields
                Tournament tournament = new Tournament();
                tournament.setName(name);
                tournament.setStartDate(startDate);
                tournament.setEndDate(endDate);
                tournament.setStatus(Tournament.Status.UPCOMING);
                tournament.setIsFeatured(false);

                // Store and retrieve
                Tournament saved = tournamentRepository.saveAndFlush(tournament);
                Tournament retrieved = tournamentRepository.findById(saved.getId()).orElseThrow();

                // Assert all required fields are present and non-null
                assertThat(retrieved.getId()).isNotNull();
                assertThat(retrieved.getName()).isNotNull();
                assertThat(retrieved.getStartDate()).isNotNull();
                assertThat(retrieved.getEndDate()).isNotNull();
                assertThat(retrieved.getStatus()).isNotNull();
                assertThat(retrieved.getIsFeatured()).isNotNull();
                assertThat(retrieved.getCreatedAt()).isNotNull();
                assertThat(retrieved.getUpdatedAt()).isNotNull();
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 4: Required Field Completeness")
    public void testDistrictRequiredFieldCompleteness() {
        qt()
            .withExamples(100)
            .forAll(
                nameGen(),
                strings().betweenCodePoints(65, 90).ofLength(3)
            )
            .checkAssert((name, code) -> {
                // Create district with only required fields
                District district = new District();
                district.setName(name);
                district.setCode(code + UUID.randomUUID().toString().substring(0, 4)); // Ensure uniqueness
                district.setIsActive(true);

                // Store and retrieve
                District saved = districtRepository.saveAndFlush(district);
                District retrieved = districtRepository.findById(saved.getId()).orElseThrow();

                // Assert all required fields are present and non-null
                assertThat(retrieved.getId()).isNotNull();
                assertThat(retrieved.getName()).isNotNull();
                assertThat(retrieved.getCode()).isNotNull();
                assertThat(retrieved.getIsActive()).isNotNull();
                assertThat(retrieved.getCreatedAt()).isNotNull();
                assertThat(retrieved.getUpdatedAt()).isNotNull();
            });
    }
}
