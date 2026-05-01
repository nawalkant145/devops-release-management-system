package com.devops.releasemgmt.service;

import com.devops.releasemgmt.dto.DeploymentRequest;
import com.devops.releasemgmt.entity.Deployment;
import com.devops.releasemgmt.entity.Release;
import com.devops.releasemgmt.entity.User;
import com.devops.releasemgmt.entity.enums.DeploymentStatus;
import com.devops.releasemgmt.entity.enums.Environment;
import com.devops.releasemgmt.repository.DeploymentRepository;
import com.devops.releasemgmt.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final ReleaseService releaseService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public DeploymentService(DeploymentRepository deploymentRepository, ReleaseService releaseService, 
                             UserRepository userRepository, AuditLogService auditLogService) {
        this.deploymentRepository = deploymentRepository;
        this.releaseService = releaseService;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<Deployment> getAllDeployments() {
        return deploymentRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Deployment> getDeploymentsByRelease(Long releaseId) {
        return deploymentRepository.findByReleaseIdOrderByCreatedAtDesc(releaseId);
    }

    public Deployment getDeploymentById(Long id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deployment not found with id: " + id));
    }

    public Deployment deploy(DeploymentRequest request) {
        Release release = releaseService.getReleaseById(request.getReleaseId());
        Environment env = Environment.valueOf(request.getEnvironment().toUpperCase());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Deployment deployment = Deployment.builder()
                .release(release)
                .environment(env)
                .status(DeploymentStatus.PENDING)
                .deployedBy(user)
                .build();

        deployment = deploymentRepository.save(deployment);
        deployment.setStatus(DeploymentStatus.IN_PROGRESS);
        deployment = deploymentRepository.save(deployment);

        deployment.setStatus(DeploymentStatus.SUCCESS);
        deployment.setCompletedAt(LocalDateTime.now());
        deployment = deploymentRepository.save(deployment);

        auditLogService.log("DEPLOY", "DEPLOYMENT", deployment.getId(),
                "Release " + release.getVersion() + " deployed to " + env.name(), username);

        return deployment;
    }

    public Deployment rollback(Long deploymentId) {
        Deployment deployment = getDeploymentById(deploymentId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long projectId = deployment.getRelease().getProject().getId();
        Optional<Deployment> previousDeployment = deploymentRepository
                .findTopByReleaseProjectIdAndEnvironmentAndStatusOrderByCreatedAtDesc(
                        projectId, deployment.getEnvironment(), DeploymentStatus.SUCCESS);

        deployment.setStatus(DeploymentStatus.ROLLED_BACK);
        deployment = deploymentRepository.save(deployment);

        auditLogService.log("ROLLBACK", "DEPLOYMENT", deployment.getId(),
                "Deployment rolled back from release " + deployment.getRelease().getVersion()
                        + " in " + deployment.getEnvironment().name(), username);

        return deployment;
    }

    public Map<String, Long> getStats() {
        long total = deploymentRepository.count();
        long success = deploymentRepository.countByStatus(DeploymentStatus.SUCCESS);
        long failed = deploymentRepository.countByStatus(DeploymentStatus.FAILED);
        long pending = deploymentRepository.countByStatus(DeploymentStatus.PENDING);
        long rolledBack = deploymentRepository.countByStatus(DeploymentStatus.ROLLED_BACK);

        return Map.of(
                "total", total,
                "success", success,
                "failed", failed,
                "pending", pending,
                "rolledBack", rolledBack
        );
    }
}
