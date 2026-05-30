package com.sigma_squad.computify.computer.service;

import com.sigma_squad.computify.computer.dto.ComputerDTO;
import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;
import com.sigma_squad.computify.computer.entity.Computer;

import java.util.List;

/**
 * IComputerService - Contract for computer management operations
 */
public interface IComputerService {
    Computer createComputer(Integer computerNumber);
    Computer getComputerById(Long id);
    Computer getComputerByNumber(Integer computerNumber);
    List<ComputerDTO> getAllComputers();
    boolean isAvailable(Long computerId);
    void markAsReserved(Long computerId, Long userId);
    void markAsInUse(Long computerId, Long userId);
    void markAsAvailable(Long computerId);
    void markAsOutOfService(Long computerId);
    ComputerStatsDTO getComputerStats();
}
