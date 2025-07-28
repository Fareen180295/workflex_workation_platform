package com.workflex.workation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_tenant", columnList = "tenant_id"),
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_action", columnList = "action")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Tenant ID cannot be null")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull(message = "Action cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @NotBlank(message = "Entity type cannot be blank")
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON string of old values

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON string of new values

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // Additional context or details

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public AuditLog(Long userId, Long tenantId, AuditAction action, String entityType, Long entityId, String entityName) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public AuditLog(Long userId, Long tenantId, AuditAction action, String entityType, Long entityId, String entityName, String details) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.details = details;
    }

    public enum AuditAction {
        // User actions
        USER_CREATED("User was created"),
        USER_UPDATED("User was updated"),
        USER_DELETED("User was deleted"),
        USER_LOGIN("User logged in"),
        USER_LOGOUT("User logged out"),
        USER_PASSWORD_CHANGED("User password was changed"),

        // Resource actions
        RESOURCE_CREATED("Resource was created"),
        RESOURCE_UPDATED("Resource was updated"),
        RESOURCE_DELETED("Resource was deleted"),
        RESOURCE_VIEWED("Resource was viewed"),
        RESOURCE_DOWNLOADED("Resource was downloaded"),

        // Tenant actions
        TENANT_CREATED("Tenant was created"),
        TENANT_UPDATED("Tenant was updated"),
        TENANT_DELETED("Tenant was deleted"),

        // System actions
        SYSTEM_LOGIN_FAILED("Login attempt failed"),
        SYSTEM_ACCESS_DENIED("Access was denied"),
        SYSTEM_RATE_LIMITED("Request was rate limited"),

        // General actions
        CREATED("Entity was created"),
        UPDATED("Entity was updated"),
        DELETED("Entity was deleted"),
        VIEWED("Entity was viewed");

        private final String description;

        AuditAction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}