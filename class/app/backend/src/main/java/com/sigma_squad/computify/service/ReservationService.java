package com.sigma_squad.computify.service;

import com.sigma_squad.computify.dto.ReservationDTO;
import com.sigma_squad.computify.entity.Reservation;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationService - Answers: "How do reservations work?"
 * Handles booking logic and reservation lifecycle.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final long RESERVATION_DURATION_MINUTES = 5;

    private final ReservationRepository reservationRepository;
    private final ComputerService computerService;

    /**
     * Create a reservation
     * Business rule: User must NOT have ACTIVE reservation
     * Business rule: Computer must be AVAILABLE
     */
    public Reservation createReservation(Long userId, Long computerId) {
        // Check if user already has active reservation
        if (reservationRepository.existsByUserIdAndStatus(userId, Reservation.ReservationStatus.ACTIVE)) {
            throw new BusinessRuleException("User already has an active reservation");
        }

        // Check if computer is available
        if (!computerService.isAvailable(computerId)) {
            throw new BusinessRuleException("Computer is not available for reservation");
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setComputerId(computerId);
        reservation.setReservedAt(Instant.now());
        reservation.setExpiresAt(Instant.now().plusSeconds(RESERVATION_DURATION_MINUTES * 60));
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        Reservation saved = reservationRepository.save(reservation);

        // Mark computer as reserved
        computerService.markAsReserved(computerId, userId);

        return saved;
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    /**
     * Get active reservation for a user
     */
    public Reservation getActiveReservationByUserId(Long userId) {
        return reservationRepository.findByUserIdAndStatus(userId, Reservation.ReservationStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("No active reservation found for user: " + userId));
    }

    /**
     * Cancel reservation
     */
    public void cancelReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            throw new BusinessRuleException("Cannot cancel non-active reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Mark computer as available
        computerService.markAsAvailable(reservation.getComputerId());
    }

    /**
     * Confirm reservation (LIBRARIAN ACTION)
     * Converts reservation to session
     */
    public void confirmReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            throw new BusinessRuleException("Cannot confirm non-active reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        // Mark computer as in use
        computerService.markAsInUse(reservation.getComputerId(), reservation.getUserId());
    }

    /**
     * Expire reservation (called by scheduler)
     */
    public void expireReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            return; // Already expired or cancelled
        }

        reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        reservationRepository.save(reservation);

        // Mark computer as available
        computerService.markAsAvailable(reservation.getComputerId());
    }

    /**
     * Get all expired ACTIVE reservations (for scheduler)
     */
    public List<Reservation> getExpiredReservations() {
        return reservationRepository.findByStatus(Reservation.ReservationStatus.ACTIVE).stream()
            .filter(Reservation::hasExpired)
            .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    public ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.fromEntity(reservation);
    }
}
