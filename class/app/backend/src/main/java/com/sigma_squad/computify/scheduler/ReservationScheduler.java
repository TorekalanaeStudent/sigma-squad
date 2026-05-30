package com.sigma_squad.computify.scheduler;

import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.service.IReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ReservationScheduler - Automatically expires old reservations
 * Runs every minute to check for expired reservations
 */
@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {

    private final IReservationService reservationService;

    /**
     * Expire reservations that have passed their expiration time
     * Runs every minute (60000 milliseconds)
     */
    @Scheduled(fixedDelay = 60000)
    public void expireReservations() {
        try {
            List<Reservation> expiredReservations = reservationService.getExpiredReservations();

            if (!expiredReservations.isEmpty()) {
                expiredReservations.forEach(reservation ->
                    reservationService.expireReservation(reservation.getId())
                );
                log.info("Expired {} reservations", expiredReservations.size());
            }
        } catch (Exception e) {
            log.error("Error expiring reservations", e);
        }
    }
}
