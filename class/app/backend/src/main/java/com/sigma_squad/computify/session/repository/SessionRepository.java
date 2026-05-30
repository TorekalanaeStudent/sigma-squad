package com.sigma_squad.computify.session.repository;

import com.sigma_squad.computify.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByUserIdAndStatus(Long userId, Session.SessionStatus status);
    List<Session> findByUserId(Long userId);
    List<Session> findByStatus(Session.SessionStatus status);
    boolean existsByUserIdAndStatus(Long userId, Session.SessionStatus status);
}
