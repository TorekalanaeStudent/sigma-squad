package com.sigma_squad.computify.session.service.impl;

import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.session.dto.SessionDTO;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.session.repository.SessionRepository;
import com.sigma_squad.computify.computer.service.IComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SessionServiceImpl - Implementation of ISessionService
 * Handles session lifecycle and usage tracking.
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

    private final SessionRepository sessionRepository;
    private final IComputerService computerService;

    @Override
    public Session startSession(Long userId, Long computerId) {
        if (sessionRepository.findByUserIdAndStatus(userId, Session.SessionStatus.ACTIVE).isPresent()) {
            throw new BusinessRuleException("User already has an active session");
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setComputerId(computerId);
        session.setStatus(Session.SessionStatus.ACTIVE);

        return sessionRepository.save(session);
    }

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
    }

    @Override
    public Session getActiveSessionByUserId(Long userId) {
        return sessionRepository.findByUserIdAndStatus(userId, Session.SessionStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("No active session found for user: " + userId));
    }

    @Override
    public void endSession(Long sessionId) {
        Session session = getSessionById(sessionId);

        if (!session.isActive()) {
            throw new BusinessRuleException("Cannot end non-active session");
        }

        session.endSession();
        sessionRepository.save(session);

        computerService.markAsAvailable(session.getComputerId());
    }

    @Override
    public List<SessionDTO> getAllActiveSessions() {
        return sessionRepository.findByStatus(Session.SessionStatus.ACTIVE).stream()
            .map(SessionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public SessionDTO toDTO(Session session) {
        return SessionDTO.fromEntity(session);
    }
}
