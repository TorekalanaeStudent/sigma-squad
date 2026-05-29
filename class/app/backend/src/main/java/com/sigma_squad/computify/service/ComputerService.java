package com.sigma_squad.computify.service;

import com.sigma_squad.computify.dto.ComputerDTO;
import com.sigma_squad.computify.entity.Computer;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ComputerService - Answers: "What is the state of a computer?"
 * Handles computer availability and status management.
 */
@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;

    /**
     * Create a new computer (LIBRARIAN ONLY - enforced at controller)
     * Business rule: computerNumber must be unique
     */
    public Computer createComputer(Integer computerNumber) {
        if (computerRepository.existsByComputerNumber(computerNumber)) {
            throw new BusinessRuleException("Computer with number " + computerNumber + " already exists");
        }

        Computer computer = new Computer();
        computer.setComputerNumber(computerNumber);
        computer.setStatus(Computer.ComputerStatus.AVAILABLE);

        return computerRepository.save(computer);
    }

    /**
     * Get computer by ID
     */
    public Computer getComputerById(Long id) {
        return computerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Computer not found with id: " + id));
    }

    /**
     * Get computer by number
     */
    public Computer getComputerByNumber(Integer computerNumber) {
        return computerRepository.findByComputerNumber(computerNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Computer not found with number: " + computerNumber));
    }

    /**
     * Get all computers
     */
    public List<ComputerDTO> getAllComputers() {
        return computerRepository.findAll().stream()
            .map(ComputerDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Business rule: Check if computer is available for reservation
     */
    public boolean isAvailable(Long computerId) {
        Computer computer = getComputerById(computerId);
        return computer.isAvailable();
    }

    /**
     * Mark computer as reserved (used by ReservationService)
     */
    public void markAsReserved(Long computerId, Long userId) {
        Computer computer = getComputerById(computerId);
        if (!computer.isAvailable()) {
            throw new BusinessRuleException("Computer is not available");
        }
        computer.setStatus(Computer.ComputerStatus.RESERVED);
        computer.setCurrentUserId(userId);
        computerRepository.save(computer);
    }

    /**
     * Mark computer as in use (used by SessionService)
     */
    public void markAsInUse(Long computerId, Long userId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.IN_USE);
        computer.setCurrentUserId(userId);
        computerRepository.save(computer);
    }

    /**
     * Mark computer as available (used by SessionService when session ends)
     */
    public void markAsAvailable(Long computerId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.AVAILABLE);
        computer.setCurrentUserId(null);
        computerRepository.save(computer);
    }

    /**
     * Mark computer as out of service (LIBRARIAN ONLY)
     */
    public void markAsOutOfService(Long computerId) {
        Computer computer = getComputerById(computerId);
        computer.setStatus(Computer.ComputerStatus.OUT_OF_SERVICE);
        computerRepository.save(computer);
    }
}
