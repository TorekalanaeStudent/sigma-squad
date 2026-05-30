package com.sigma_squad.computify.reservation.service;

import com.sigma_squad.computify.reservation.dto.ReservationDTO;
import com.sigma_squad.computify.reservation.entity.Reservation;

import java.util.List;

/**
 * IReservationService - Contract for reservation management operations
 */
public interface IReservationService {
    Reservation createReservation(Long userId, Long computerId);
    Reservation getReservationById(Long id);
    Reservation getActiveReservationByUserId(Long userId);
    List<Reservation> getUserReservations(Long userId);
    List<Reservation> getAllReservations();
    void cancelReservation(Long reservationId);
    void confirmReservation(Long reservationId);
    void expireReservation(Long reservationId);
    void updateReservationExpiresAt(Long reservationId, Long expiresAtSeconds);
    List<Reservation> getExpiredReservations();
    ReservationDTO toDTO(Reservation reservation);
}
