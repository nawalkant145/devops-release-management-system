package com.devops.releasemgmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReleaseRequest {
    @NotBlank(message = "Version is required")
    private String version;
    private String description;
    private String releaseNotes;
    private String artifactUrl;
    @NotNull(message = "Project ID is required")
    private Long projectId;

    public ReleaseRequest() {}
    public ReleaseRequest(String version, String description, String releaseNotes, String artifactUrl, Long projectId) {
        this.version = version;
        this.description = description;
        this.releaseNotes = releaseNotes;
        this.artifactUrl = artifactUrl;
        this.projectId = projectId;
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReleaseNotes() { return releaseNotes; }
    public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }
    public String getArtifactUrl() { return artifactUrl; }
    public void setArtifactUrl(String artifactUrl) { this.artifactUrl = artifactUrl; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}
