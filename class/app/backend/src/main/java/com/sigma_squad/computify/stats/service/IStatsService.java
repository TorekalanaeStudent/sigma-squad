package com.sigma_squad.computify.stats.service;

import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;

/**
 * IStatsService - Contract for statistics operations
 */
public interface IStatsService {
    ComputerStatsDTO getComputerStats();
}
