package com.sigma_squad.computify.reservation.controller;

import com.sigma_squad.computify.reservation.dto.CreateReservationRequest;
import com.sigma_squad.computify.reservation.dto.ReservationDTO;
import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
        @RequestBody CreateReservationRequest request,
        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        Reservation reservation = reservationService.createReservation(userId, request.computerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.toDTO(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationService.toDTO(reservation));
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
}
