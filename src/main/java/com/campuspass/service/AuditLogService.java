package com.campuspass.service;

import com.campuspass.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {
    void log(Long actorUserId, String actorUsername, String actorRole, String action, String entityName, Long entityId, String details);
    List<AuditLog> getRecentLogs();
    Page<AuditLog> getAllLogs(Pageable pageable);
}
