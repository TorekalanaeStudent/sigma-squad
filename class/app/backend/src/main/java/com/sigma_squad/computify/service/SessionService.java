package com.sigma_squad.computify.service;

import com.sigma_squad.computify.dto.SessionDTO;
import com.sigma_squad.computify.entity.Session;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SessionService - Answers: "How is a computer currently being used?"
 * Handles session lifecycle and usage tracking.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ComputerService computerService;

    /**
     * Start a new session (from confirmed reservation)
     * Business rule: One session per user only
     */
    public Session startSession(Long userId, Long computerId) {
        // Check if user already has active session
        if (sessionRepository.findByUserIdAndStatus(userId, Session.SessionStatus.ACTIVE).isPresent()) {
            throw new BusinessRuleException("User already has an active session");
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setComputerId(computerId);
        session.setStatus(Session.SessionStatus.ACTIVE);

        return sessionRepository.save(session);
    }

    /**
     * Get session by ID
     */
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
    }

    /**
     * Get active session for a user
     */
    public Session getActiveSessionByUserId(Long userId) {
        return sessionRepository.findByUserIdAndStatus(userId, Session.SessionStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("No active session found for user: " + userId));
    }

    /**
     * End session (LIBRARIAN ACTION)
     * Business rule: Session must be ACTIVE
     */
    public void endSession(Long sessionId) {
        Session session = getSessionById(sessionId);

        if (!session.isActive()) {
            throw new BusinessRuleException("Cannot end non-active session");
        }

        session.endSession();
        sessionRepository.save(session);

        // Mark computer as available
        computerService.markAsAvailable(session.getComputerId());
    }

    /**
     * Get all active sessions
     */
    public List<SessionDTO> getAllActiveSessions() {
        return sessionRepository.findByStatus(Session.SessionStatus.ACTIVE).stream()
            .map(SessionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    public SessionDTO toDTO(Session session) {
        return SessionDTO.fromEntity(session);
    }
}
