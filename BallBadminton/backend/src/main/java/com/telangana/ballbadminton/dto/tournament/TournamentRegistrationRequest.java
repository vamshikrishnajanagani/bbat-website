package com.telangana.ballbadminton.dto.tournament;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for tournament registration requests
 */
public class TournamentRegistrationRequest {

    @NotNull(message = "Player ID is required")
    private UUID playerId;

    private BigDecimal paymentAmount;

    private String paymentReference;

    private String notes;

    // Constructors
    public TournamentRegistrationRequest() {}

    // Getters and Setters
    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
