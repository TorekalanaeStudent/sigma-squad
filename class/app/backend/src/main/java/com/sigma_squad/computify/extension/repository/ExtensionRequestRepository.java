package com.sigma_squad.computify.extension.repository;

import com.sigma_squad.computify.extension.entity.ExtensionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtensionRequestRepository extends JpaRepository<ExtensionRequest, Long> {
    List<ExtensionRequest> findByStatus(ExtensionRequest.ExtensionStatus status);
    Optional<ExtensionRequest> findBySessionIdAndStatus(Long sessionId, ExtensionRequest.ExtensionStatus status);
    List<ExtensionRequest> findByUserId(Long userId);
}
