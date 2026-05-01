package com.devops.releasemgmt.service;

import com.devops.releasemgmt.dto.ProjectRequest;
import com.devops.releasemgmt.entity.Project;
import com.devops.releasemgmt.entity.User;
import com.devops.releasemgmt.repository.ProjectRepository;
import com.devops.releasemgmt.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, AuditLogService auditLogService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    public Project createProject(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new RuntimeException("Project with this name already exists");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .repoUrl(request.getRepoUrl())
                .createdBy(user)
                .build();

        project = projectRepository.save(project);

        auditLogService.log("CREATE", "PROJECT", project.getId(),
                "Project created: " + project.getName(), username);

        return project;
    }

    public Project updateProject(Long id, ProjectRequest request) {
        Project project = getProjectById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setRepoUrl(request.getRepoUrl());
        project = projectRepository.save(project);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogService.log("UPDATE", "PROJECT", project.getId(),
                "Project updated: " + project.getName(), username);

        return project;
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        auditLogService.log("DELETE", "PROJECT", project.getId(),
                "Project deleted: " + project.getName(), username);

        projectRepository.delete(project);
    }
}
