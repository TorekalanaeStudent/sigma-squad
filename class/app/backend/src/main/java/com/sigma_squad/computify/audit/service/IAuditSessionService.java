package com.sigma_squad.computify.audit.service;

import com.sigma_squad.computify.audit.dto.SessionAuditDTO;

import java.util.List;

public interface IAuditSessionService {
    List<SessionAuditDTO> getAllSessions();
}
