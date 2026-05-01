package com.devops.releasemgmt.controller;

import com.devops.releasemgmt.dto.ProjectRequest;
import com.devops.releasemgmt.entity.Project;
import com.devops.releasemgmt.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        List<Map<String, Object>> response = projects.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(toMap(projectService.getProjectById(id)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(toMap(projectService.createProject(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProject(@PathVariable Long id,
                                                              @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(toMap(projectService.updateProject(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(Map.of("message", "Project deleted successfully"));
    }

    private Map<String, Object> toMap(Project p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("description", p.getDescription());
        map.put("repoUrl", p.getRepoUrl());
        map.put("createdAt", p.getCreatedAt());
        map.put("updatedAt", p.getUpdatedAt());
        if (p.getCreatedBy() != null) {
            map.put("createdBy", p.getCreatedBy().getUsername());
        }
        map.put("releaseCount", p.getReleases() != null ? p.getReleases().size() : 0);
        return map;
    }
}
