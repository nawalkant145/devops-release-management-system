package com.devops.releasemgmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeploymentRequest {
    @NotNull(message = "Release ID is required")
    private Long releaseId;
    @NotBlank(message = "Environment is required")
    private String environment;

    public DeploymentRequest() {}
    public DeploymentRequest(Long releaseId, String environment) {
        this.releaseId = releaseId;
        this.environment = environment;
    }

    public Long getReleaseId() { return releaseId; }
    public void setReleaseId(Long releaseId) { this.releaseId = releaseId; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
}
