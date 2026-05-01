package com.devops.releasemgmt.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;
    private String description;
    private String repoUrl;

    public ProjectRequest() {}
    public ProjectRequest(String name, String description, String repoUrl) {
        this.name = name;
        this.description = description;
        this.repoUrl = repoUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
}
