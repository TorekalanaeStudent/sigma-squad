package com.sigma_squad.computify.reservation.controller;

import com.sigma_squad.computify.reservation.dto.CreateReservationRequest;
import com.sigma_squad.computify.reservation.dto.ReservationDTO;
import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations.stream()
            .map(reservationService::toDTO)
            .toList());
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
        @RequestBody CreateReservationRequest request,
        Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        Reservation reservation = reservationService.createReservation(userId, request.computerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.toDTO(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations.stream()
            .map(reservationService::toDTO)
            .toList());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
    }

    @PutMapping("/{id}/update-expiry")
    public ResponseEntity<ReservationDTO> updateReservationExpiry(
            @PathVariable Long id,
            @RequestParam Long expiresAtSeconds) {
        reservationService.updateReservationExpiresAt(id, expiresAtSeconds);
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
    }
}
