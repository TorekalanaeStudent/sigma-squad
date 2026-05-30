package com.sigma_squad.computify.session.controller;

import com.sigma_squad.computify.session.dto.SessionDTO;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ISessionService sessionService;

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllActiveSessions() {
        List<SessionDTO> sessions = sessionService.getAllActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable Long id) {
        Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SessionDTO> getUserActiveSession(@PathVariable Long userId) {
        try {
            Session session = sessionService.getActiveSessionByUserId(userId);
            return ResponseEntity.ok(sessionService.toDTO(session));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<SessionDTO> endSession(@PathVariable Long id) {
        sessionService.endSession(id);
        Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
}
