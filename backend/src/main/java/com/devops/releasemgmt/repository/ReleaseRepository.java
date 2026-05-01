package com.devops.releasemgmt.repository;

import com.devops.releasemgmt.entity.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReleaseRepository extends JpaRepository<Release, Long> {
    List<Release> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    long countByProjectId(Long projectId);
}
