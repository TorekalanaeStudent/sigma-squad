package com.sigma_squad.computify.computer.repository;

import com.sigma_squad.computify.computer.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Optional<Computer> findByComputerNumber(Integer computerNumber);
    boolean existsByComputerNumber(Integer computerNumber);
    long countByStatus(Computer.ComputerStatus status);
}
