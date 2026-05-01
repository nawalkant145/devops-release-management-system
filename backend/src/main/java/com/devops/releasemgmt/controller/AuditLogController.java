package com.devops.releasemgmt.controller;

import com.devops.releasemgmt.entity.AuditLog;
import com.devops.releasemgmt.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLogs() {
        List<AuditLog> logs = auditLogService.getAllLogs();
        List<Map<String, Object>> response = logs.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entity}/{entityId}")
    public ResponseEntity<List<Map<String, Object>>> getLogsByEntity(
            @PathVariable String entity, @PathVariable Long entityId) {
        List<AuditLog> logs = auditLogService.getLogsByEntity(entity, entityId);
        List<Map<String, Object>> response = logs.stream().map(this::toMap).toList();
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toMap(AuditLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("action", log.getAction());
        map.put("entity", log.getEntity());
        map.put("entityId", log.getEntityId());
        map.put("details", log.getDetails());
        map.put("performedBy", log.getPerformedBy());
        map.put("createdAt", log.getCreatedAt());
        return map;
    }
}
