package com.sigma_squad.computify.controller;

import com.sigma_squad.computify.dto.CreateReservationRequest;
import com.sigma_squad.computify.dto.ReservationDTO;
import com.sigma_squad.computify.entity.Reservation;
import com.sigma_squad.computify.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReservationController - Receptionist for reservation endpoints
 * Receives request → validates → passes to ReservationService → returns response
 */
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * POST /reservations
     * Create a new reservation
     * Note: userId from JWT token (to be added with security config)
     */
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
        @RequestBody CreateReservationRequest request,
        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: Extract userId from JWT token instead of header
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        Reservation reservation = reservationService.createReservation(userId, request.getComputerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.toDTO(reservation));
    }

    /**
     * GET /reservations/{id}
     * Get reservation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
    }

    /**
     * POST /reservations/{id}/cancel
     * Cancel a reservation
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /reservations/{id}/confirm
     * Confirm reservation (LIBRARIAN ONLY)
     * Converts reservation to session
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
    }
}
