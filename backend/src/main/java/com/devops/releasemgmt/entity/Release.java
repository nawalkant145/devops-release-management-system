package com.devops.releasemgmt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "releases")
public class Release {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String version;

    @Column(length = 1000)
    private String description;

    @Column(name = "release_notes", length = 2000)
    private String releaseNotes;

    @Column(name = "artifact_url")
    private String artifactUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deployment> deployments = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Release() {}

    public Release(String version, String description, String releaseNotes, String artifactUrl, Project project, User createdBy) {
        this.version = version;
        this.description = description;
        this.releaseNotes = releaseNotes;
        this.artifactUrl = artifactUrl;
        this.project = project;
        this.createdBy = createdBy;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReleaseNotes() { return releaseNotes; }
    public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }
    public String getArtifactUrl() { return artifactUrl; }
    public void setArtifactUrl(String artifactUrl) { this.artifactUrl = artifactUrl; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public List<Deployment> getDeployments() { return deployments; }
    public void setDeployments(List<Deployment> deployments) { this.deployments = deployments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Simple Builder
    public static ReleaseBuilder builder() { return new ReleaseBuilder(); }

    public static class ReleaseBuilder {
        private String version;
        private String description;
        private String releaseNotes;
        private String artifactUrl;
        private Project project;
        private User createdBy;

        public ReleaseBuilder version(String version) { this.version = version; return this; }
        public ReleaseBuilder description(String description) { this.description = description; return this; }
        public ReleaseBuilder releaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; return this; }
        public ReleaseBuilder artifactUrl(String artifactUrl) { this.artifactUrl = artifactUrl; return this; }
        public ReleaseBuilder project(Project project) { this.project = project; return this; }
        public ReleaseBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public Release build() { return new Release(version, description, releaseNotes, artifactUrl, project, createdBy); }
    }
}
