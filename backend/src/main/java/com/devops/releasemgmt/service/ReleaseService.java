package com.devops.releasemgmt.service;

import com.devops.releasemgmt.dto.ReleaseRequest;
import com.devops.releasemgmt.entity.Project;
import com.devops.releasemgmt.entity.Release;
import com.devops.releasemgmt.entity.User;
import com.devops.releasemgmt.repository.ReleaseRepository;
import com.devops.releasemgmt.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ReleaseService(ReleaseRepository releaseRepository, ProjectService projectService, 
                          UserRepository userRepository, AuditLogService auditLogService) {
        this.releaseRepository = releaseRepository;
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<Release> getAllReleases() {
        return releaseRepository.findAll();
    }

    public List<Release> getReleasesByProject(Long projectId) {
        return releaseRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public Release getReleaseById(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Release not found with id: " + id));
    }

    public Release createRelease(ReleaseRequest request) {
        Project project = projectService.getProjectById(request.getProjectId());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Release release = Release.builder()
                .version(request.getVersion())
                .description(request.getDescription())
                .releaseNotes(request.getReleaseNotes())
                .artifactUrl(request.getArtifactUrl())
                .project(project)
                .createdBy(user)
                .build();

        release = releaseRepository.save(release);
        auditLogService.log("CREATE", "RELEASE", release.getId(),
                "Release " + release.getVersion() + " created for project: " + project.getName(), username);
        return release;
    }

    public void deleteRelease(Long id) {
        Release release = getReleaseById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogService.log("DELETE", "RELEASE", release.getId(),
                "Release " + release.getVersion() + " deleted", username);
        releaseRepository.delete(release);
    }
}
