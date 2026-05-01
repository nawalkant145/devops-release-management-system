package com.devops.releasemgmt.controller;

import com.devops.releasemgmt.dto.ReleaseRequest;
import com.devops.releasemgmt.entity.Release;
import com.devops.releasemgmt.service.ReleaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllReleases() {
        List<Release> releases = releaseService.getAllReleases();
        List<Map<String, Object>> response = releases.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Map<String, Object>>> getReleasesByProject(@PathVariable Long projectId) {
        List<Release> releases = releaseService.getReleasesByProject(projectId);
        List<Map<String, Object>> response = releases.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRelease(@PathVariable Long id) {
        return ResponseEntity.ok(toMap(releaseService.getReleaseById(id)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRelease(@Valid @RequestBody ReleaseRequest request) {
        return ResponseEntity.ok(toMap(releaseService.createRelease(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteRelease(@PathVariable Long id) {
        releaseService.deleteRelease(id);
        return ResponseEntity.ok(Map.of("message", "Release deleted successfully"));
    }

    private Map<String, Object> toMap(Release r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("version", r.getVersion());
        map.put("description", r.getDescription());
        map.put("releaseNotes", r.getReleaseNotes());
        map.put("artifactUrl", r.getArtifactUrl());
        map.put("createdAt", r.getCreatedAt());
        if (r.getProject() != null) {
            map.put("projectId", r.getProject().getId());
            map.put("projectName", r.getProject().getName());
        }
        if (r.getCreatedBy() != null) {
            map.put("createdBy", r.getCreatedBy().getUsername());
        }
        map.put("deploymentCount", r.getDeployments() != null ? r.getDeployments().size() : 0);
        return map;
    }
}
