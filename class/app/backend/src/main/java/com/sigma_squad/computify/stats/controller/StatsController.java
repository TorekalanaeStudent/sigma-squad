package com.sigma_squad.computify.stats.controller;

import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;
import com.sigma_squad.computify.stats.service.IStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatsController {

    private final IStatsService statsService;

    @GetMapping("/computer-availability")
    public ResponseEntity<ComputerStatsDTO> getComputerAvailability() {
        return ResponseEntity.ok(statsService.getComputerStats());
    }
}
