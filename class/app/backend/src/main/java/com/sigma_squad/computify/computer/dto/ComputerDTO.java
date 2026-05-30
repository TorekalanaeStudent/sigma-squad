package com.sigma_squad.computify.computer.dto;

import com.sigma_squad.computify.computer.entity.Computer;

/**
 * ComputerDTO - DTO for computer data transfer
 */
public record ComputerDTO(
    Long id,
    Integer computerNumber,
    String status,
    Long currentUserId
) {
    public static ComputerDTO fromEntity(Computer computer) {
        return new ComputerDTO(
            computer.getId(),
            computer.getComputerNumber(),
            computer.getStatus().toString(),
            computer.getCurrentUserId()
        );
    }
}
