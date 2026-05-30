package com.sigma_squad.computify.config;

import com.sigma_squad.computify.computer.entity.Computer;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final ComputerRepository computerRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (computerRepository.count() == 0) {
            initializeComputers();
        }
    }

    private void initializeComputers() {
        for (int i = 1; i <= 10; i++) {
            Computer computer = Computer.builder()
                .computerNumber(i)
                .status(Computer.ComputerStatus.AVAILABLE)
                .build();
            computerRepository.save(computer);
        }
    }
}
