package com.workflex.workation.repository;

import com.workflex.workation.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByTenantId(Long tenantId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndUserId(Long tenantId, Long userId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndAction(Long tenantId, AuditLog.AuditAction action, Pageable pageable);

    Page<AuditLog> findByTenantIdAndEntityType(Long tenantId, String entityType, Pageable pageable);

    Page<AuditLog> findByTenantIdAndEntityTypeAndEntityId(Long tenantId, String entityType, Long entityId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND a.timestamp BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByTenantIdAndTimestampBetween(@Param("tenantId") Long tenantId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate, 
                                                     Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND a.userId = :userId AND a.timestamp BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByTenantIdAndUserIdAndTimestampBetween(@Param("tenantId") Long tenantId, 
                                                              @Param("userId") Long userId,
                                                              @Param("startDate") LocalDateTime startDate, 
                                                              @Param("endDate") LocalDateTime endDate, 
                                                              Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.tenantId = :tenantId AND a.action = :action")
    long countByTenantIdAndAction(@Param("tenantId") Long tenantId, @Param("action") AuditLog.AuditAction action);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.tenantId = :tenantId AND a.timestamp >= :since")
    long countByTenantIdSince(@Param("tenantId") Long tenantId, @Param("since") LocalDateTime since);

    // Get distinct actions for a tenant
    @Query("SELECT DISTINCT a.action FROM AuditLog a WHERE a.tenantId = :tenantId")
    List<AuditLog.AuditAction> findDistinctActionsByTenantId(@Param("tenantId") Long tenantId);

    // Get distinct entity types for a tenant
    @Query("SELECT DISTINCT a.entityType FROM AuditLog a WHERE a.tenantId = :tenantId")
    List<String> findDistinctEntityTypesByTenantId(@Param("tenantId") Long tenantId);

    // Get recent activities
    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId ORDER BY a.timestamp DESC")
    Page<AuditLog> findRecentByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    // Get user activities
    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND a.userId = :userId ORDER BY a.timestamp DESC")
    Page<AuditLog> findByTenantIdAndUserIdOrderByTimestampDesc(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);

    // Advanced search with multiple filters
    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId " +
           "AND (:userId IS NULL OR a.userId = :userId) " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:entityType IS NULL OR a.entityType = :entityType) " +
           "AND (:entityId IS NULL OR a.entityId = :entityId) " +
           "AND (:startDate IS NULL OR a.timestamp >= :startDate) " +
           "AND (:endDate IS NULL OR a.timestamp <= :endDate) " +
           "ORDER BY a.timestamp DESC")
    Page<AuditLog> findWithFilters(@Param("tenantId") Long tenantId,
                                   @Param("userId") Long userId,
                                   @Param("action") AuditLog.AuditAction action,
                                   @Param("entityType") String entityType,
                                   @Param("entityId") Long entityId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    // Security monitoring queries
    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND a.action IN :suspiciousActions AND a.timestamp >= :since")
    List<AuditLog> findSuspiciousActivities(@Param("tenantId") Long tenantId, 
                                           @Param("suspiciousActions") List<AuditLog.AuditAction> suspiciousActions, 
                                           @Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND a.ipAddress = :ipAddress AND a.timestamp >= :since")
    List<AuditLog> findByTenantIdAndIpAddressSince(@Param("tenantId") Long tenantId, 
                                                   @Param("ipAddress") String ipAddress, 
                                                   @Param("since") LocalDateTime since);
}