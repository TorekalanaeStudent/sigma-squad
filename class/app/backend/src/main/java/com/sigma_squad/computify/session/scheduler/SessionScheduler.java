package com.sigma_squad.computify.session.scheduler;

import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.session.repository.SessionRepository;
import com.sigma_squad.computify.shared.websocket.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * SessionScheduler - Background scheduler for session lifecycle management
 * Tasks:
 * 1. Check sessions expiring in 5 minutes → notify student
 * 2. Check expired sessions → end session, free computer, notify admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionScheduler {

    private final SessionRepository sessionRepository;
    private final ISessionService sessionService;
    private final NotificationService notificationService;

    /**
     * Check for sessions expiring in 5 minutes and notify students
     * Runs every 30 seconds
     */
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    public void checkSessions5MinuteWarning() {
        try {
            List<Session> activeSessions = sessionRepository.findByStatus(Session.SessionStatus.ACTIVE);

            for (Session session : activeSessions) {
                if (!session.isActive()) continue;

                long minutesRemaining = session.getMinutesRemaining();

                // Notify when exactly 5 minutes remaining
                if (minutesRemaining == 5) {
                    notificationService.notifyStudent(
                        session.getUserId(),
                        "⏰ Session Expiring Soon",
                        "Your session expires in 5 minutes. Click 'Extend' to add 1 more hour."
                    );

                    log.info("5-minute warning sent to user {} for session {}", session.getUserId(), session.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error in checkSessions5MinuteWarning scheduler", e);
        }
    }

    /**
     * Check for expired sessions and end them
     * Runs every 30 seconds
     */
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    public void checkExpiredSessions() {
        try {
            List<Session> activeSessions = sessionRepository.findByStatus(Session.SessionStatus.ACTIVE);

            for (Session session : activeSessions) {
                if (!session.isActive()) continue;

                long minutesRemaining = session.getMinutesRemaining();

                // End session if time expired
                if (minutesRemaining <= 0) {
                    Long sessionId = session.getId();
                    Long userId = session.getUserId();
                    Long computerId = session.getComputerId();

                    // End the session (marks ENDED, frees computer)
                    sessionService.endSession(sessionId);

                    // Notify student their session ended
                    notificationService.notifyStudent(
                        userId,
                        "Session Ended",
                        "Your computer session has ended. The computer is now available for other users."
                    );

                    // Notify admin that session ended
                    notificationService.notifyAdmins(
                        "Session Auto-Ended",
                        "User " + userId + " session on Computer " + computerId + " has expired and been ended."
                    );

                    log.info("Session {} auto-ended for user {}", sessionId, userId);
                }
            }
        } catch (Exception e) {
            log.error("Error in checkExpiredSessions scheduler", e);
        }
    }
}
