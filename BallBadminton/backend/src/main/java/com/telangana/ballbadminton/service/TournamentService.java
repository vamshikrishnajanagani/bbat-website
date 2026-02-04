package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.tournament.*;
import com.telangana.ballbadminton.entity.*;
import com.telangana.ballbadminton.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for tournament management operations
 */
@Service
@Transactional
public class TournamentService {

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final DistrictRepository districtRepository;
    private final EmailService emailService;

    public TournamentService(TournamentRepository tournamentRepository,
                           PlayerRepository playerRepository,
                           DistrictRepository districtRepository,
                           EmailService emailService) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.districtRepository = districtRepository;
        this.emailService = emailService;
    }

    // Tournament CRUD Operations

    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments() {
        logger.debug("Fetching all tournaments");
        return tournamentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> getTournaments(int page, int size, String sortBy, String sortDir) {
        logger.debug("Fetching tournaments with pagination - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return tournamentRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Cacheable(value = "tournaments", key = "#id")
    @Transactional(readOnly = true)
    public Optional<TournamentResponse> getTournamentById(UUID id) {
        logger.debug("Fetching tournament by ID: {}", id);
        return tournamentRepository.findById(id)
                .map(this::convertToResponse);
    }

    @CacheEvict(value = "tournaments", allEntries = true)
    public TournamentResponse createTournament(TournamentRequest request) {
        logger.debug("Creating new tournament: {}", request.getName());
        
        validateTournamentDates(request.getStartDate(), request.getEndDate());
        
        Tournament tournament = new Tournament();
        mapRequestToEntity(request, tournament);
        
        Tournament savedTournament = tournamentRepository.save(tournament);
        logger.info("Tournament created successfully with ID: {}", savedTournament.getId());
        
        return convertToResponse(savedTournament);
    }

    @Caching(evict = {
        @CacheEvict(value = "tournaments", key = "#id"),
        @CacheEvict(value = "tournaments", allEntries = true)
    })
    public TournamentResponse updateTournament(UUID id, TournamentRequest request) {
        logger.debug("Updating tournament: {}", id);
        
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + id));
        
        validateTournamentDates(request.getStartDate(), request.getEndDate());
        
        mapRequestToEntity(request, tournament);
        
        Tournament updatedTournament = tournamentRepository.save(tournament);
        logger.info("Tournament updated successfully: {}", id);
        
        return convertToResponse(updatedTournament);
    }

    @Caching(evict = {
        @CacheEvict(value = "tournaments", key = "#id"),
        @CacheEvict(value = "tournaments", allEntries = true)
    })
    public void deleteTournament(UUID id) {
        logger.debug("Deleting tournament: {}", id);
        
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + id));
        
        tournamentRepository.delete(tournament);
        logger.info("Tournament deleted successfully: {}", id);
    }

    // Tournament Status Management

    public TournamentResponse updateTournamentStatus(UUID id, Tournament.Status status) {
        logger.debug("Updating tournament status: {} to {}", id, status);
        
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + id));
        
        Tournament.Status oldStatus = tournament.getStatus();
        tournament.setStatus(status);
        
        Tournament updatedTournament = tournamentRepository.save(tournament);
        logger.info("Tournament status updated from {} to {}: {}", oldStatus, status, id);
        
        // Send notifications for status changes
        if (status == Tournament.Status.REGISTRATION_OPEN) {
            notifyRegistrationOpen(tournament);
        } else if (status == Tournament.Status.ONGOING) {
            notifyTournamentStarting(tournament);
        } else if (status == Tournament.Status.COMPLETED) {
            notifyTournamentCompleted(tournament);
        }
        
        return convertToResponse(updatedTournament);
    }

    // Tournament Registration

    public TournamentRegistrationResponse registerPlayer(UUID tournamentId, TournamentRegistrationRequest request) {
        logger.debug("Registering player {} for tournament {}", request.getPlayerId(), tournamentId);
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));
        
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + request.getPlayerId()));
        
        // Validate registration
        if (!tournament.isRegistrationOpen()) {
            throw new IllegalStateException("Registration is not open for this tournament");
        }
        
        if (!tournament.hasAvailableSlots()) {
            throw new IllegalStateException("Tournament is full");
        }
        
        // Check for duplicate registration
        boolean alreadyRegistered = tournament.getRegistrations().stream()
                .anyMatch(reg -> reg.getPlayer().getId().equals(player.getId()) && reg.isActive());
        
        if (alreadyRegistered) {
            throw new IllegalStateException("Player is already registered for this tournament");
        }
        
        // Create registration
        TournamentRegistration registration = new TournamentRegistration(tournament, player);
        registration.setPaymentAmount(request.getPaymentAmount());
        registration.setPaymentReference(request.getPaymentReference());
        registration.setNotes(request.getNotes());
        
        tournament.addRegistration(registration);
        tournamentRepository.save(tournament);
        
        logger.info("Player {} registered successfully for tournament {}", player.getId(), tournamentId);
        
        // Send confirmation email
        notifyPlayerRegistration(tournament, player, registration);
        
        return convertRegistrationToResponse(registration);
    }

    @Transactional(readOnly = true)
    public List<TournamentRegistrationResponse> getTournamentRegistrations(UUID tournamentId) {
        logger.debug("Fetching registrations for tournament: {}", tournamentId);
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));
        
        return tournament.getRegistrations().stream()
                .map(this::convertRegistrationToResponse)
                .collect(Collectors.toList());
    }

    public TournamentRegistrationResponse updateRegistrationStatus(UUID tournamentId, UUID registrationId, 
                                                                   TournamentRegistration.RegistrationStatus status) {
        logger.debug("Updating registration status: {} to {}", registrationId, status);
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));
        
        TournamentRegistration registration = tournament.getRegistrations().stream()
                .filter(reg -> reg.getId().equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Registration not found with ID: " + registrationId));
        
        registration.setStatus(status);
        tournamentRepository.save(tournament);
        
        logger.info("Registration status updated to {}: {}", status, registrationId);
        
        return convertRegistrationToResponse(registration);
    }

    // Tournament Bracket Generation

    public BracketResponse generateBracket(UUID tournamentId) {
        logger.debug("Generating bracket for tournament: {}", tournamentId);
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));
        
        List<TournamentRegistration> activeRegistrations = tournament.getRegistrations().stream()
                .filter(TournamentRegistration::isActive)
                .collect(Collectors.toList());
        
        if (activeRegistrations.isEmpty()) {
            throw new IllegalStateException("No active registrations found for tournament");
        }
        
        // Shuffle players for random seeding
        Collections.shuffle(activeRegistrations);
        
        BracketResponse bracket = new BracketResponse();
        bracket.setTournamentId(tournament.getId());
        bracket.setTournamentName(tournament.getName());
        
        int playerCount = activeRegistrations.size();
        int totalRounds = (int) Math.ceil(Math.log(playerCount) / Math.log(2));
        bracket.setTotalRounds(totalRounds);
        
        List<BracketResponse.Round> rounds = new ArrayList<>();
        
        // Generate first round
        BracketResponse.Round firstRound = new BracketResponse.Round();
        firstRound.setRoundNumber(1);
        firstRound.setRoundName(getRoundName(1, totalRounds));
        
        List<BracketResponse.Match> firstRoundMatches = new ArrayList<>();
        int matchNumber = 1;
        
        for (int i = 0; i < activeRegistrations.size(); i += 2) {
            BracketResponse.Match match = new BracketResponse.Match();
            match.setMatchNumber(matchNumber++);
            match.setStatus(BracketResponse.MatchStatus.PENDING);
            
            TournamentRegistration player1Reg = activeRegistrations.get(i);
            match.setPlayer1Id(player1Reg.getPlayer().getId());
            match.setPlayer1Name(player1Reg.getPlayer().getName());
            
            if (i + 1 < activeRegistrations.size()) {
                TournamentRegistration player2Reg = activeRegistrations.get(i + 1);
                match.setPlayer2Id(player2Reg.getPlayer().getId());
                match.setPlayer2Name(player2Reg.getPlayer().getName());
            } else {
                // Bye - player advances automatically
                match.setWinnerId(player1Reg.getPlayer().getId());
                match.setWinnerName(player1Reg.getPlayer().getName());
                match.setStatus(BracketResponse.MatchStatus.WALKOVER);
            }
            
            firstRoundMatches.add(match);
        }
        
        firstRound.setMatches(firstRoundMatches);
        rounds.add(firstRound);
        
        // Generate subsequent rounds (empty for now)
        for (int round = 2; round <= totalRounds; round++) {
            BracketResponse.Round nextRound = new BracketResponse.Round();
            nextRound.setRoundNumber(round);
            nextRound.setRoundName(getRoundName(round, totalRounds));
            nextRound.setMatches(new ArrayList<>());
            rounds.add(nextRound);
        }
        
        bracket.setRounds(rounds);
        
        logger.info("Bracket generated successfully for tournament: {}", tournamentId);
        
        return bracket;
    }

    // Tournament Filtering and Search

    @Cacheable(value = "tournaments", key = "'upcoming'")
    @Transactional(readOnly = true)
    public List<TournamentResponse> getUpcomingTournaments() {
        logger.debug("Fetching upcoming tournaments");
        return tournamentRepository.findAll().stream()
                .filter(Tournament::isUpcoming)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getOngoingTournaments() {
        logger.debug("Fetching ongoing tournaments");
        return tournamentRepository.findAll().stream()
                .filter(Tournament::isOngoing)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getCompletedTournaments() {
        logger.debug("Fetching completed tournaments");
        return tournamentRepository.findAll().stream()
                .filter(Tournament::isCompleted)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "tournaments", key = "'featured'")
    @Transactional(readOnly = true)
    public List<TournamentResponse> getFeaturedTournaments() {
        logger.debug("Fetching featured tournaments");
        return tournamentRepository.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsFeatured()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getTournamentsByDistrict(UUID districtId) {
        logger.debug("Fetching tournaments for district: {}", districtId);
        return tournamentRepository.findAll().stream()
                .filter(t -> t.getDistrict() != null && t.getDistrict().getId().equals(districtId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getTournamentsByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching tournaments between {} and {}", startDate, endDate);
        return tournamentRepository.findAll().stream()
                .filter(t -> !t.getStartDate().isBefore(startDate) && !t.getEndDate().isAfter(endDate))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper Methods

    private void mapRequestToEntity(TournamentRequest request, Tournament tournament) {
        tournament.setName(request.getName());
        tournament.setDescription(request.getDescription());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setVenue(request.getVenue());
        tournament.setRegistrationStartDate(request.getRegistrationStartDate());
        tournament.setRegistrationEndDate(request.getRegistrationEndDate());
        tournament.setMaxParticipants(request.getMaxParticipants());
        tournament.setEntryFee(request.getEntryFee());
        tournament.setPrizeMoney(request.getPrizeMoney());
        
        if (request.getStatus() != null) {
            tournament.setStatus(request.getStatus());
        }
        
        tournament.setTournamentType(request.getTournamentType());
        tournament.setAgeCategory(request.getAgeCategory());
        tournament.setGenderCategory(request.getGenderCategory());
        
        if (request.getIsFeatured() != null) {
            tournament.setIsFeatured(request.getIsFeatured());
        }
        
        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new IllegalArgumentException("District not found with ID: " + request.getDistrictId()));
            tournament.setDistrict(district);
        }
    }

    private TournamentResponse convertToResponse(Tournament tournament) {
        TournamentResponse response = new TournamentResponse();
        response.setId(tournament.getId());
        response.setName(tournament.getName());
        response.setDescription(tournament.getDescription());
        response.setStartDate(tournament.getStartDate());
        response.setEndDate(tournament.getEndDate());
        response.setVenue(tournament.getVenue());
        response.setRegistrationStartDate(tournament.getRegistrationStartDate());
        response.setRegistrationEndDate(tournament.getRegistrationEndDate());
        response.setMaxParticipants(tournament.getMaxParticipants());
        response.setEntryFee(tournament.getEntryFee());
        response.setPrizeMoney(tournament.getPrizeMoney());
        response.setStatus(tournament.getStatus());
        response.setTournamentType(tournament.getTournamentType());
        response.setAgeCategory(tournament.getAgeCategory());
        response.setGenderCategory(tournament.getGenderCategory());
        response.setIsFeatured(tournament.getIsFeatured());
        response.setCurrentRegistrationCount(tournament.getCurrentRegistrationCount());
        response.setHasAvailableSlots(tournament.hasAvailableSlots());
        response.setIsRegistrationOpen(tournament.isRegistrationOpen());
        response.setDurationInDays(tournament.getDurationInDays());
        response.setCreatedAt(tournament.getCreatedAt());
        response.setUpdatedAt(tournament.getUpdatedAt());
        
        if (tournament.getDistrict() != null) {
            response.setDistrictId(tournament.getDistrict().getId());
            response.setDistrictName(tournament.getDistrict().getName());
        }
        
        return response;
    }

    private TournamentRegistrationResponse convertRegistrationToResponse(TournamentRegistration registration) {
        TournamentRegistrationResponse response = new TournamentRegistrationResponse();
        response.setId(registration.getId());
        response.setTournamentId(registration.getTournament().getId());
        response.setTournamentName(registration.getTournament().getName());
        response.setPlayerId(registration.getPlayer().getId());
        response.setPlayerName(registration.getPlayer().getName());
        response.setRegistrationDate(registration.getRegistrationDate());
        response.setPaymentStatus(registration.getPaymentStatus());
        response.setPaymentAmount(registration.getPaymentAmount());
        response.setPaymentReference(registration.getPaymentReference());
        response.setStatus(registration.getStatus());
        response.setNotes(registration.getNotes());
        response.setCreatedAt(registration.getCreatedAt());
        response.setUpdatedAt(registration.getUpdatedAt());
        return response;
    }

    private void validateTournamentDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }

    private String getRoundName(int roundNumber, int totalRounds) {
        if (roundNumber == totalRounds) {
            return "Final";
        } else if (roundNumber == totalRounds - 1) {
            return "Semi-Final";
        } else if (roundNumber == totalRounds - 2) {
            return "Quarter-Final";
        } else {
            return "Round " + roundNumber;
        }
    }

    // Notification Methods

    private void notifyRegistrationOpen(Tournament tournament) {
        logger.debug("Sending registration open notifications for tournament: {}", tournament.getId());
        // Implementation would send emails to interested players
    }

    private void notifyTournamentStarting(Tournament tournament) {
        logger.debug("Sending tournament starting notifications for tournament: {}", tournament.getId());
        tournament.getRegistrations().stream()
                .filter(TournamentRegistration::isActive)
                .forEach(reg -> {
                    try {
                        emailService.sendNotificationEmail(
                            reg.getPlayer().getContactEmail(),
                            "Tournament Starting: " + tournament.getName(),
                            String.format("The tournament '%s' is starting on %s at %s. Good luck!",
                                        tournament.getName(), tournament.getStartDate(), tournament.getVenue())
                        );
                    } catch (Exception e) {
                        logger.error("Failed to send tournament starting notification to player {}: {}", 
                                   reg.getPlayer().getId(), e.getMessage());
                    }
                });
    }

    private void notifyTournamentCompleted(Tournament tournament) {
        logger.debug("Sending tournament completed notifications for tournament: {}", tournament.getId());
        tournament.getRegistrations().stream()
                .filter(TournamentRegistration::isActive)
                .forEach(reg -> {
                    try {
                        emailService.sendNotificationEmail(
                            reg.getPlayer().getContactEmail(),
                            "Tournament Completed: " + tournament.getName(),
                            String.format("The tournament '%s' has been completed. Thank you for participating!",
                                        tournament.getName())
                        );
                    } catch (Exception e) {
                        logger.error("Failed to send tournament completed notification to player {}: {}", 
                                   reg.getPlayer().getId(), e.getMessage());
                    }
                });
    }

    private void notifyPlayerRegistration(Tournament tournament, Player player, TournamentRegistration registration) {
        logger.debug("Sending registration confirmation to player: {}", player.getId());
        try {
            emailService.sendNotificationEmail(
                player.getContactEmail(),
                "Registration Confirmed: " + tournament.getName(),
                String.format("Your registration for '%s' has been confirmed. Tournament starts on %s at %s.",
                            tournament.getName(), tournament.getStartDate(), tournament.getVenue())
            );
        } catch (Exception e) {
            logger.error("Failed to send registration confirmation to player {}: {}", player.getId(), e.getMessage());
        }
    }
}
