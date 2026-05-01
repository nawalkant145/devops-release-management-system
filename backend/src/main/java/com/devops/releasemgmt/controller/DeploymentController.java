package com.devops.releasemgmt.controller;

import com.devops.releasemgmt.dto.DeploymentRequest;
import com.devops.releasemgmt.entity.Deployment;
import com.devops.releasemgmt.service.DeploymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDeployments() {
        List<Deployment> deployments = deploymentService.getAllDeployments();
        List<Map<String, Object>> response = deployments.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDeployment(@PathVariable Long id) {
        return ResponseEntity.ok(toMap(deploymentService.getDeploymentById(id)));
    }

    @GetMapping("/release/{releaseId}")
    public ResponseEntity<List<Map<String, Object>>> getDeploymentsByRelease(@PathVariable Long releaseId) {
        List<Deployment> deployments = deploymentService.getDeploymentsByRelease(releaseId);
        List<Map<String, Object>> response = deployments.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> deploy(@Valid @RequestBody DeploymentRequest request) {
        return ResponseEntity.ok(toMap(deploymentService.deploy(request)));
    }

    @PostMapping("/{id}/rollback")
    public ResponseEntity<Map<String, Object>> rollback(@PathVariable Long id) {
        return ResponseEntity.ok(toMap(deploymentService.rollback(id)));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(deploymentService.getStats());
    }

    private Map<String, Object> toMap(Deployment d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", d.getId());
        map.put("environment", d.getEnvironment().name());
        map.put("status", d.getStatus().name());
        map.put("deployedAt", d.getDeployedAt());
        map.put("completedAt", d.getCompletedAt());
        map.put("createdAt", d.getCreatedAt());
        if (d.getRelease() != null) {
            map.put("releaseId", d.getRelease().getId());
            map.put("releaseVersion", d.getRelease().getVersion());
            if (d.getRelease().getProject() != null) {
                map.put("projectName", d.getRelease().getProject().getName());
            }
        }
        if (d.getDeployedBy() != null) {
            map.put("deployedBy", d.getDeployedBy().getUsername());
        }
        return map;
    }
}
