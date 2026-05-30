package com.sigma_squad.computify.session.service;

import com.sigma_squad.computify.session.dto.SessionDTO;
import com.sigma_squad.computify.session.entity.Session;

import java.util.List;

/**
 * ISessionService - Contract for session management operations
 */
public interface ISessionService {
    Session startSession(Long userId, Long computerId);
    Session getSessionById(Long id);
    Session getActiveSessionByUserId(Long userId);
    void endSession(Long sessionId);
    void extendSession(Long sessionId, long durationMinutes);
    List<SessionDTO> getAllActiveSessions();
    SessionDTO toDTO(Session session);
}
