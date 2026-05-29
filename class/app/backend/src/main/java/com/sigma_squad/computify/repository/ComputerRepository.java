package com.sigma_squad.computify.repository;

import com.sigma_squad.computify.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Optional<Computer> findByComputerNumber(Integer computerNumber);
    boolean existsByComputerNumber(Integer computerNumber);
}
