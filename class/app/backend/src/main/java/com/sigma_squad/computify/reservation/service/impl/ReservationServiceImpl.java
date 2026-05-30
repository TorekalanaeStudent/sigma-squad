package com.sigma_squad.computify.reservation.service.impl;

import com.sigma_squad.computify.reservation.service.IReservationService;
import com.sigma_squad.computify.reservation.dto.ReservationDTO;
import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.reservation.repository.ReservationRepository;
import com.sigma_squad.computify.computer.service.IComputerService;
import com.sigma_squad.computify.history.service.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationServiceImpl - Implementation of IReservationService
 * Handles booking logic and reservation lifecycle.
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private static final long RESERVATION_DURATION_MINUTES = 5;

    private final ReservationRepository reservationRepository;
    private final IComputerService computerService;
    private final IAuditLogService auditLogService;

    @Override
    public Reservation createReservation(Long userId, Long computerId) {
        if (reservationRepository.existsByUserIdAndStatus(userId, Reservation.ReservationStatus.ACTIVE)) {
            throw new BusinessRuleException("User already has an active reservation");
        }

        if (!computerService.isAvailable(computerId)) {
            throw new BusinessRuleException("Computer is not available for reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setComputerId(computerId);
        reservation.setReservedAt(Instant.now());
        reservation.setExpiresAt(Instant.now().plusSeconds(RESERVATION_DURATION_MINUTES * 60));
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        Reservation saved = reservationRepository.save(reservation);

        computerService.markAsReserved(computerId, userId);

        auditLogService.log(userId, saved.getId(), "CREATE", "Reservation created for Computer " + computerId);

        return saved;
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    @Override
    public Reservation getActiveReservationByUserId(Long userId) {
        return reservationRepository.findByUserIdAndStatus(userId, Reservation.ReservationStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("No active reservation found for user: " + userId));
    }

    @Override
    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            throw new BusinessRuleException("Cannot cancel non-active reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        computerService.markAsAvailable(reservation.getComputerId());

        auditLogService.log(reservation.getUserId(), reservationId, "CANCEL", "Reservation cancelled");
    }

    @Override
    public void confirmReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            throw new BusinessRuleException("Cannot confirm non-active reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        computerService.markAsInUse(reservation.getComputerId(), reservation.getUserId());

        auditLogService.log(reservation.getUserId(), reservationId, "ACCEPT", "Reservation accepted and session started");
    }

    @Override
    public void updateReservationExpiresAt(Long reservationId, Long expiresAtSeconds) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            throw new BusinessRuleException("Cannot update non-active reservation");
        }

        reservation.setExpiresAt(Instant.ofEpochSecond(expiresAtSeconds));
        reservationRepository.save(reservation);
    }

    @Override
    public void expireReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isActive()) {
            return;
        }

        reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        reservationRepository.save(reservation);

        computerService.markAsAvailable(reservation.getComputerId());

        auditLogService.log(reservation.getUserId(), reservationId, "EXPIRE", "Reservation expired");
    }

    @Override
    public List<Reservation> getExpiredReservations() {
        return reservationRepository.findByStatus(Reservation.ReservationStatus.ACTIVE).stream()
            .filter(Reservation::hasExpired)
            .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.fromEntity(reservation);
    }
}
