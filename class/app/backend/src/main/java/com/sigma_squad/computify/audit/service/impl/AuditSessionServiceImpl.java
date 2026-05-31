package com.sigma_squad.computify.audit.service.impl;

import com.sigma_squad.computify.audit.dto.SessionAuditDTO;
import com.sigma_squad.computify.audit.service.IAuditSessionService;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.repository.UserRepository;
import com.sigma_squad.computify.computer.entity.Computer;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditSessionServiceImpl implements IAuditSessionService {
    
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ComputerRepository computerRepository;

    @Override
    public List<SessionAuditDTO> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private SessionAuditDTO convertToDTO(Session session) {
        User user = userRepository.findById(session.getUserId()).orElse(null);
        Computer computer = computerRepository.findById(session.getComputerId()).orElse(null);
        
        long minutesUsed = 0;
        if (session.getEndTime() != null) {
            minutesUsed = (session.getEndTime().toEpochMilli() - session.getStartTime().toEpochMilli()) / 60000;
        }
        
        return SessionAuditDTO.builder()
            .id(session.getId())
            .userid(session.getUserId())
            .username(user != null ? user.getName() : "Unknown User")
            .computerid(session.getComputerId())
            .computernumber(computer != null ? computer.getComputerNumber().toString() : "Unknown")
            .starttime(session.getStartTime())
            .endtime(session.getEndTime())
            .minutesused(minutesUsed)
            .build();
    }
}
