package com.devops.releasemgmt.entity;

import com.devops.releasemgmt.entity.enums.DeploymentStatus;
import com.devops.releasemgmt.entity.enums.Environment;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deployments")
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by")
    private User deployedBy;

    @Column(name = "rollback_release_id")
    private Long rollbackFromReleaseId;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Deployment() {}

    public Deployment(Environment environment, DeploymentStatus status, Release release, User deployedBy) {
        this.environment = environment;
        this.status = status;
        this.release = release;
        this.deployedBy = deployedBy;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.deployedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Environment getEnvironment() { return environment; }
    public void setEnvironment(Environment environment) { this.environment = environment; }
    public DeploymentStatus getStatus() { return status; }
    public void setStatus(DeploymentStatus status) { this.status = status; }
    public Release getRelease() { return release; }
    public void setRelease(Release release) { this.release = release; }
    public User getDeployedBy() { return deployedBy; }
    public void setDeployedBy(User deployedBy) { this.deployedBy = deployedBy; }
    public Long getRollbackFromReleaseId() { return rollbackFromReleaseId; }
    public void setRollbackFromReleaseId(Long rollbackFromReleaseId) { this.rollbackFromReleaseId = rollbackFromReleaseId; }
    public LocalDateTime getDeployedAt() { return deployedAt; }
    public void setDeployedAt(LocalDateTime deployedAt) { this.deployedAt = deployedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Simple Builder
    public static DeploymentBuilder builder() { return new DeploymentBuilder(); }

    public static class DeploymentBuilder {
        private Environment environment;
        private DeploymentStatus status;
        private Release release;
        private User deployedBy;

        public DeploymentBuilder environment(Environment environment) { this.environment = environment; return this; }
        public DeploymentBuilder status(DeploymentStatus status) { this.status = status; return this; }
        public DeploymentBuilder release(Release release) { this.release = release; return this; }
        public DeploymentBuilder deployedBy(User deployedBy) { this.deployedBy = deployedBy; return this; }
        public Deployment build() { return new Deployment(environment, status, release, deployedBy); }
    }
}
