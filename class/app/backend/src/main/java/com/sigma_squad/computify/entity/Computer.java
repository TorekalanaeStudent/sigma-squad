package com.sigma_squad.computify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "computers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer computerNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Default
    private ComputerStatus status = ComputerStatus.AVAILABLE;

    @Column
    private Long currentUserId; // Nullable - tracks who's using it

    /**
     * Business rule: Derived from Reservation/Session
     * Stored for MVP performance optimization
     */
    public boolean isAvailable() {
        return status == ComputerStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == ComputerStatus.RESERVED;
    }

    public boolean isInUse() {
        return status == ComputerStatus.IN_USE;
    }

    public enum ComputerStatus {
        AVAILABLE,
        RESERVED,
        IN_USE,
        OUT_OF_SERVICE
    }
}
