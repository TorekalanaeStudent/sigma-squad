package com.sigma_squad.computify.computer.dto;

/**
 * ComputerStatsDTO - DTO for computer statistics
 */
public record ComputerStatsDTO(
    long totalComputers,
    long availableComputers,
    long reservedComputers,
    long inUseComputers,
    long outOfServiceComputers
) {}
