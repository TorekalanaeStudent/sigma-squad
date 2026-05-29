package com.sigma_squad.computify.dto;

import com.sigma_squad.computify.entity.Computer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputerDTO {
    private Long id;
    private Integer computerNumber;
    private String status;
    private Long currentUserId;

    public static ComputerDTO fromEntity(Computer computer) {
        return new ComputerDTO(
            computer.getId(),
            computer.getComputerNumber(),
            computer.getStatus().toString(),
            computer.getCurrentUserId()
        );
    }
}
