package com.campuspass.service.impl;

import com.campuspass.entity.AuditLog;
import com.campuspass.repository.AuditLogRepository;
import com.campuspass.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long actorUserId, String actorUsername, String actorRole, String action, String entityName, Long entityId, String details) {
        AuditLog auditLog = new AuditLog(
                actorUserId,
                actorUsername != null ? actorUsername : "SYSTEM",
                actorRole != null ? actorRole : "SYSTEM",
                action,
                entityName,
                entityId,
                details
        );
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
