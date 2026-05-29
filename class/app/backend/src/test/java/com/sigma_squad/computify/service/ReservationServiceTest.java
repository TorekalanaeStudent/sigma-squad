package com.sigma_squad.computify.service;

import com.sigma_squad.computify.entity.Reservation;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ComputerService computerService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = Reservation.builder()
                .id(1L)
                .userId(1L)
                .computerId(1L)
                .status(Reservation.ReservationStatus.ACTIVE)
                .reservedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }

    @Test
    void testCreateReservationSuccess() {
        // Given
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.createReservation(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getComputerId());
        assertEquals(Reservation.ReservationStatus.ACTIVE, result.getStatus());
        verify(computerService, times(1)).markAsReserved(1L, 1L);
    }

    @Test
    void testCreateReservationUserAlreadyHasActive() {
        // Given
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            reservationService.createReservation(1L, 1L)
        );
    }

    @Test
    void testCreateReservationComputerNotAvailable() {
        // Given
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(false);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            reservationService.createReservation(1L, 1L)
        );
    }

    @Test
    void testCancelReservationSuccess() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        reservationService.cancelReservation(1L);

        // Then
        assertEquals(Reservation.ReservationStatus.CANCELLED, testReservation.getStatus());
        verify(computerService, times(1)).markAsAvailable(1L);
    }

    @Test
    void testGetReservationByIdNotFound() {
        // Given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            reservationService.getReservationById(999L)
        );
    }
}
