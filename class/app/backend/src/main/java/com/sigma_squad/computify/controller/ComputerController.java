package com.sigma_squad.computify.controller;

import com.sigma_squad.computify.dto.ComputerDTO;
import com.sigma_squad.computify.service.ComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ComputerController - Receptionist for computer endpoints
 * Receives request → validates → passes to ComputerService → returns response
 */
@RestController
@RequestMapping("/api/computers")
@RequiredArgsConstructor
public class ComputerController {

    private final ComputerService computerService;

    /**
     * GET /computers
     * Get all computers with their availability status
     */
    @GetMapping
    public ResponseEntity<List<ComputerDTO>> getAllComputers() {
        List<ComputerDTO> computers = computerService.getAllComputers();
        return ResponseEntity.ok(computers);
    }

    /**
     * POST /computers
     * Create a new computer (LIBRARIAN ONLY)
     * Note: Security/role validation to be added at controller level
     */
    @PostMapping
    public ResponseEntity<ComputerDTO> createComputer(@RequestBody ComputerDTO request) {
        com.sigma_squad.computify.entity.Computer computer = computerService.createComputer(request.getComputerNumber());
        return ResponseEntity.ok(ComputerDTO.fromEntity(computer));
    }

    /**
     * GET /computers/{id}
     * Get computer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComputerDTO> getComputerById(@PathVariable Long id) {
        com.sigma_squad.computify.entity.Computer computer = computerService.getComputerById(id);
        return ResponseEntity.ok(ComputerDTO.fromEntity(computer));
    }
}
