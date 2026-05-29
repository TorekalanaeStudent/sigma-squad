package com.sigma_squad.computify.controller;

import com.sigma_squad.computify.dto.SessionDTO;
import com.sigma_squad.computify.entity.Session;
import com.sigma_squad.computify.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SessionController - Receptionist for session endpoints
 * Receives request → validates → passes to SessionService → returns response
 * LIBRARIAN ONLY for most operations
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * GET /sessions
     * Get all active sessions (LIBRARIAN ONLY)
     */
    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllActiveSessions() {
        List<SessionDTO> sessions = sessionService.getAllActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * GET /sessions/{id}
     * Get session by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable Long id) {
        Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }

    /**
     * POST /sessions/{id}/end
     * End a session (LIBRARIAN ONLY)
     * Marks computer as available
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<SessionDTO> endSession(@PathVariable Long id) {
        sessionService.endSession(id);
        Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
}
