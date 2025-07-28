package com.workflex.workation.service;

import com.workflex.workation.dto.AuditLogDto;
import com.workflex.workation.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditService {

    void logAction(String action, Long userId, Long tenantId, String details);

    Page<AuditLogDto> getAuditLogs(Long tenantId, Pageable pageable);

    Page<AuditLogDto> getAuditLogsByUser(Long userId, Long tenantId, Pageable pageable);

    Page<AuditLogDto> getAuditLogsByAction(String action, Long tenantId, Pageable pageable);

    Page<AuditLogDto> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                             Long tenantId, Pageable pageable);

    void logResourceCreated(Long resourceId, Long userId, Long tenantId);

    void logResourceUpdated(Long resourceId, Long userId, Long tenantId);

    void logResourceDeleted(Long resourceId, Long userId, Long tenantId);

    void logUserCreated(Long userId, Long createdByUserId, Long tenantId);

    void logUserDeleted(Long userId, Long deletedByUserId, Long tenantId);

    void logUserLogin(Long userId, Long tenantId);

    void logUserLogout(Long userId, Long tenantId);
}