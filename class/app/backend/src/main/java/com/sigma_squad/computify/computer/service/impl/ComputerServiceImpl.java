package com.sigma_squad.computify.computer.service.impl;

import com.sigma_squad.computify.computer.service.IComputerService;
import com.sigma_squad.computify.computer.dto.ComputerDTO;
import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;
import com.sigma_squad.computify.computer.entity.Computer;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ComputerServiceImpl - Implementation of IComputerService
 * Handles computer availability and status management.
 */
@Service
@RequiredArgsConstructor
public class ComputerServiceImpl implements IComputerService {

    private final ComputerRepository computerRepository;

    @Override
    public Computer createComputer(Integer computerNumber) {
        if (computerRepository.existsByComputerNumber(computerNumber)) {
            throw new BusinessRuleException("Computer with number " + computerNumber + " already exists");
        }

        Computer computer = new Computer();
        computer.setComputerNumber(computerNumber);
        computer.setStatus(Computer.ComputerStatus.AVAILABLE);

        return computerRepository.save(computer);
    }

    @Override
    public Computer getComputerById(Long id) {
        return computerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Computer not found with id: " + id));
    }

    @Override
    public Computer getComputerByNumber(Integer computerNumber) {
        return computerRepository.findByComputerNumber(computerNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Computer not found with number: " + computerNumber));
    }

    @Override
    public List<ComputerDTO> getAllComputers() {
        return computerRepository.findAll().stream()
            .sorted((c1, c2) -> c1.getComputerNumber().compareTo(c2.getComputerNumber()))
            .map(ComputerDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailable(Long computerId) {
        Computer computer = getComputerById(computerId);
        return computer.isAvailable();
    }

    @Override
    public void markAsReserved(Long computerId, Long userId) {
        Computer computer = getComputerById(computerId);
        if (!computer.isAvailable()) {
            throw new BusinessRuleException("Computer is not available");
        }
        computer.setStatus(Computer.ComputerStatus.RESERVED);
        computer.setCurrentUserId(userId);
        computerRepository.save(computer);
    }

    @Override
    public void markAsInUse(Long computerId, Long userId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.IN_USE);
        computer.setCurrentUserId(userId);
        computerRepository.save(computer);
    }

    @Override
    public void markAsAvailable(Long computerId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.AVAILABLE);
        computer.setCurrentUserId(null);
        computerRepository.save(computer);
    }

    @Override
    public void markAsOutOfService(Long computerId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.OUT_OF_SERVICE);
        computerRepository.save(computer);
    }

    @Override
    public ComputerStatsDTO getComputerStats() {
        List<Computer> allComputers = computerRepository.findAll();

        long total = allComputers.size();
        long available = allComputers.stream()
            .filter(Computer::isAvailable)
            .count();
        long reserved = allComputers.stream()
            .filter(c -> c.getStatus() == Computer.ComputerStatus.RESERVED)
            .count();
        long inUse = allComputers.stream()
            .filter(c -> c.getStatus() == Computer.ComputerStatus.IN_USE)
            .count();
        long outOfService = allComputers.stream()
            .filter(c -> c.getStatus() == Computer.ComputerStatus.OUT_OF_SERVICE)
            .count();

        return new ComputerStatsDTO(total, available, reserved, inUse, outOfService);
    }
}
