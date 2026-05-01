package com.devops.releasemgmt.repository;

import com.devops.releasemgmt.entity.Deployment;
import com.devops.releasemgmt.entity.enums.DeploymentStatus;
import com.devops.releasemgmt.entity.enums.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByReleaseIdOrderByCreatedAtDesc(Long releaseId);
    List<Deployment> findAllByOrderByCreatedAtDesc();
    long countByStatus(DeploymentStatus status);
    Optional<Deployment> findTopByReleaseProjectIdAndEnvironmentAndStatusOrderByCreatedAtDesc(
            Long projectId, Environment environment, DeploymentStatus status);
}
