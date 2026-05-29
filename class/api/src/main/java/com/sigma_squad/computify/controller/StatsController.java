package com.sigma_squad.computify.controller;

import com.sigma_squad.computify.dto.ComputerStatsDTO;
import com.sigma_squad.computify.service.ComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatsController {

    private final ComputerService computerService;

    @GetMapping("/computer-availability")
    public ResponseEntity<ComputerStatsDTO> getComputerAvailability() {
        return ResponseEntity.ok(computerService.getComputerStats());
    }
}
