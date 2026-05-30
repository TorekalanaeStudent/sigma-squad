package com.sigma_squad.computify.session.dto;

/**
 * SessionExtendRequest - Request body for extending a session
 * @param durationMinutes - Minutes to extend (default 60 if null)
 */
public record SessionExtendRequest(
    Long durationMinutes
) {}
