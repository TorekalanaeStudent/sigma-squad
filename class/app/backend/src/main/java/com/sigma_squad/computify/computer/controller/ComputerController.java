package com.sigma_squad.computify.computer.controller;

import com.sigma_squad.computify.computer.dto.ComputerDTO;
import com.sigma_squad.computify.computer.service.IComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/computers")
@RequiredArgsConstructor
public class ComputerController {

    private final IComputerService computerService;

    @GetMapping
    public ResponseEntity<List<ComputerDTO>> getAllComputers() {
        List<ComputerDTO> computers = computerService.getAllComputers();
        return ResponseEntity.ok(computers);
    }

    @PostMapping
    public ResponseEntity<ComputerDTO> createComputer(@RequestBody ComputerDTO request) {
        com.sigma_squad.computify.computer.entity.Computer computer = computerService.createComputer(request.computerNumber());
        return ResponseEntity.ok(ComputerDTO.fromEntity(computer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComputerDTO> getComputerById(@PathVariable Long id) {
        com.sigma_squad.computify.computer.entity.Computer computer = computerService.getComputerById(id);
        return ResponseEntity.ok(ComputerDTO.fromEntity(computer));
    }
}
