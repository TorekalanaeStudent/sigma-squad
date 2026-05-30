package com.sigma_squad.computify.session.controller;

import com.sigma_squad.computify.session.dto.SessionDTO;
import com.sigma_squad.computify.session.dto.SessionExtendRequest;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SessionController - Phase 3 API endpoints for session management
 * Handles student operations (end-early, extend, view) and admin management (remove-user, list)
 */
@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ISessionService sessionService;

    /**
     * Get all active sessions (admin only)
     * GET /api/sessions
     */
    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllActiveSessions() {
        List<SessionDTO> sessions = sessionService.getAllActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get session by ID
     * GET /api/sessions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable Long id) {
        try {
            Session session = sessionService.getSessionById(id);
            return ResponseEntity.ok(sessionService.toDTO(session));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get current user's active session
     * GET /api/sessions/active
     */
    @GetMapping("/user/active")
    public ResponseEntity<SessionDTO> getUserActiveSession(Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            Session session = sessionService.getActiveSessionByUserId(userId);
            return ResponseEntity.ok(sessionService.toDTO(session));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Get user's active session by userId (legacy endpoint)
     * GET /api/sessions/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SessionDTO> getUserActiveSessionById(@PathVariable Long userId) {
        try {
            Session session = sessionService.getActiveSessionByUserId(userId);
            return ResponseEntity.ok(sessionService.toDTO(session));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * End session (legacy endpoint)
     * POST /api/sessions/{id}/end
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<SessionDTO> endSession(@PathVariable Long id) {
        try {
            sessionService.endSession(id);
            Session session = sessionService.getSessionById(id);
            return ResponseEntity.ok(sessionService.toDTO(session));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * End session early - Student clicks "Early Out" button
     * POST /api/sessions/{sessionId}/end-early
     * @param sessionId Session to end
     * @param authentication JWT user info (verify ownership)
     * @return Updated session after ending
     */
    @PostMapping("/{sessionId}/end-early")
    public ResponseEntity<?> endSessionEarly(
            @PathVariable Long sessionId,
            Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            Session session = sessionService.getSessionById(sessionId);

            // Verify user owns this session
            if (!session.getUserId().equals(userId)) {
                throw new BusinessRuleException("Cannot end session you don't own");
            }

            sessionService.endSession(sessionId);
            Session ended = sessionService.getSessionById(sessionId);
            log.info("User {} ended session {} early", userId, sessionId);
            return ResponseEntity.ok(sessionService.toDTO(ended));
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Extend session by 1 hour
     * POST /api/sessions/{sessionId}/extend
     * @param sessionId Session to extend
     * @param request Extension duration (default 60 minutes)
     * @param authentication JWT user info
     * @return Updated session with new end time
     */
    @PostMapping("/{sessionId}/extend")
    public ResponseEntity<?> extendSession(
            @PathVariable Long sessionId,
            @RequestBody(required = false) SessionExtendRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            Session session = sessionService.getSessionById(sessionId);

            // Verify user owns this session
            if (!session.getUserId().equals(userId)) {
                throw new BusinessRuleException("Cannot extend session you don't own");
            }

            // Extend by requested duration (default 60 minutes, no limit)
            long durationMinutes = (request != null && request.durationMinutes() != null) ?
                request.durationMinutes() : 60;

            sessionService.extendSession(sessionId, durationMinutes);
            Session updated = sessionService.getSessionById(sessionId);

            log.info("User {} extended session {} by {} minutes", userId, sessionId, durationMinutes);
            return ResponseEntity.ok(sessionService.toDTO(updated));
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove user from session - Admin forcefully ends any session
     * POST /api/sessions/{sessionId}/remove-user
     * @param sessionId Session to remove user from
     * @param authentication JWT user info (verify admin)
     * @return Success message
     */
    @PostMapping("/{sessionId}/remove-user")
    public ResponseEntity<?> removeUserFromSession(
            @PathVariable Long sessionId,
            Authentication authentication) {
        try {
            // TODO: Add @Secured("ROLE_ADMIN") or verify is_admin in auth
            Session session = sessionService.getSessionById(sessionId);
            Long affectedUserId = session.getUserId();
            Long computerId = session.getComputerId();

            sessionService.endSession(sessionId);
            log.info("Admin removed user {} from session {} (computer {})", affectedUserId, sessionId, computerId);
            return ResponseEntity.ok("User " + affectedUserId + " removed from session. Computer " + computerId + " is now available");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
