package com.sigma_squad.computify.reservation.controller;

import com.sigma_squad.computify.reservation.dto.ReservationDTO;
import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.service.IReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationController
 * Tests all controller endpoints including the new /history endpoint
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private IReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private Reservation testReservation;
    private ReservationDTO testReservationDTO;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUserId(1L);
        testReservation.setComputerId(1L);
        testReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        testReservation.setReservedAt(Instant.now());
        testReservation.setExpiresAt(Instant.now().plusSeconds(300));

        testReservationDTO = ReservationDTO.fromEntity(testReservation);
    }

    @Test
    void testGetReservationHistory_Success() {
        // Given
        List<Reservation> historyReservations = new ArrayList<>();
        historyReservations.add(testReservation);

        Reservation expiredReservation = new Reservation();
        expiredReservation.setId(2L);
        expiredReservation.setUserId(2L);
        expiredReservation.setComputerId(2L);
        expiredReservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        expiredReservation.setReservedAt(Instant.now().minusSeconds(600));
        expiredReservation.setExpiresAt(Instant.now().minusSeconds(300));

        historyReservations.add(expiredReservation);

        when(reservationService.getReservationHistory()).thenReturn(historyReservations);
        when(reservationService.toDTO(any(Reservation.class)))
            .thenAnswer(invocation -> ReservationDTO.fromEntity((Reservation) invocation.getArgument(0)));

        // When
        ResponseEntity<List<ReservationDTO>> response = reservationController.getReservationHistory();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reservationService, times(1)).getReservationHistory();
    }

    @Test
    void testGetReservationHistory_Empty() {
        // Given
        when(reservationService.getReservationHistory()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<List<ReservationDTO>> response = reservationController.getReservationHistory();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(reservationService, times(1)).getReservationHistory();
    }

    @Test
    void testGetReservationHistory_OnlyNonActiveReservations() {
        // Given
        List<Reservation> historyReservations = new ArrayList<>();

        // Add confirmed reservation
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(1L);
        confirmedReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        confirmedReservation.setReservedAt(Instant.now());
        confirmedReservation.setExpiresAt(Instant.now().plusSeconds(300));
        historyReservations.add(confirmedReservation);

        // Add cancelled reservation
        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setId(2L);
        cancelledReservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        cancelledReservation.setReservedAt(Instant.now());
        cancelledReservation.setExpiresAt(Instant.now().plusSeconds(300));
        historyReservations.add(cancelledReservation);

        // Add expired reservation
        Reservation expiredReservation = new Reservation();
        expiredReservation.setId(3L);
        expiredReservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        expiredReservation.setReservedAt(Instant.now().minusSeconds(600));
        expiredReservation.setExpiresAt(Instant.now().minusSeconds(300));
        historyReservations.add(expiredReservation);

        when(reservationService.getReservationHistory()).thenReturn(historyReservations);
        when(reservationService.toDTO(any(Reservation.class)))
            .thenAnswer(invocation -> ReservationDTO.fromEntity((Reservation) invocation.getArgument(0)));

        // When
        ResponseEntity<List<ReservationDTO>> response = reservationController.getReservationHistory();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().size());

        // Verify no ACTIVE reservations in response
        boolean hasActiveStatus = response.getBody().stream()
            .anyMatch(dto -> "ACTIVE".equals(dto.status()));
        assertFalse(hasActiveStatus, "History should not contain ACTIVE reservations");

        verify(reservationService, times(1)).getReservationHistory();
    }
}
