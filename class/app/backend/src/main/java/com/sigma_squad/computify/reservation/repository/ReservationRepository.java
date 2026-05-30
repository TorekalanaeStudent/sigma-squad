package com.sigma_squad.computify.reservation.repository;

import com.sigma_squad.computify.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByIdAndStatus(Long id, Reservation.ReservationStatus status);
    Optional<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    List<Reservation> findByUserId(Long userId);
    boolean existsByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);
}
