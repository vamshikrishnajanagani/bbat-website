package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing tournament registration
 * Links players to tournaments with payment and status information
 */
@Entity
@Table(name = "tournament_registrations", 
       uniqueConstraints = @UniqueConstraint(
           name = "uk_tournament_player", 
           columnNames = {"tournament_id", "player_id"}
       ),
       indexes = {
           @Index(name = "idx_tournament_registrations_tournament", columnList = "tournament_id"),
           @Index(name = "idx_tournament_registrations_player", columnList = "player_id"),
           @Index(name = "idx_tournament_registrations_status", columnList = "status"),
           @Index(name = "idx_tournament_registrations_payment", columnList = "payment_status"),
           @Index(name = "idx_tournament_registrations_date", columnList = "registration_date")
       })
public class TournamentRegistration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tournament_registration_tournament"))
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tournament_registration_player"))
    private Player player;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20, nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Enums
    public enum PaymentStatus {
        PENDING("Pending"),
        PAID("Paid"),
        REFUNDED("Refunded"),
        CANCELLED("Cancelled");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RegistrationStatus {
        REGISTERED("Registered"),
        CONFIRMED("Confirmed"),
        WITHDRAWN("Withdrawn"),
        DISQUALIFIED("Disqualified");

        private final String displayName;

        RegistrationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public TournamentRegistration() {}

    public TournamentRegistration(Tournament tournament, Player player) {
        this.tournament = tournament;
        this.player = player;
        this.registrationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
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

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    public boolean isActive() {
        return status == RegistrationStatus.REGISTERED || status == RegistrationStatus.CONFIRMED;
    }

    public boolean isPaymentComplete() {
        return paymentStatus == PaymentStatus.PAID;
    }

    public boolean canWithdraw() {
        return isActive() && tournament != null && tournament.isRegistrationOpen();
    }

    public void confirmRegistration() {
        if (status == RegistrationStatus.REGISTERED) {
            this.status = RegistrationStatus.CONFIRMED;
        }
    }

    public void withdraw() {
        if (canWithdraw()) {
            this.status = RegistrationStatus.WITHDRAWN;
        }
    }

    public void processPayment(BigDecimal amount, String reference) {
        this.paymentAmount = amount;
        this.paymentReference = reference;
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void refundPayment() {
        if (paymentStatus == PaymentStatus.PAID) {
            this.paymentStatus = PaymentStatus.REFUNDED;
        }
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "tournament=" + (tournament != null ? tournament.getName() : null) +
                ", player=" + (player != null ? player.getName() : null) +
                ", registrationDate=" + registrationDate +
                ", paymentStatus=" + paymentStatus +
                ", status=" + status +
                '}';
    }
}