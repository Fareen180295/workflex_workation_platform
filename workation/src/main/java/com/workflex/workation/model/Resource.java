package com.workflex.workation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources", indexes = {
    @Index(name = "idx_resource_tenant", columnList = "tenant_id"),
    @Index(name = "idx_resource_owner", columnList = "owner_id"),
    @Index(name = "idx_resource_name", columnList = "name")
})
@SQLDelete(sql = "UPDATE resources SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Resource name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Owner ID cannot be null")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotNull(message = "Tenant ID cannot be null")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Many-to-One relationship with User (owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;

    // Additional resource metadata
    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "tags")
    private String tags; // JSON string for flexibility

    @Column(name = "file_path")
    private String filePath; // For file-based resources

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "access_level")
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    public Resource(String name, String description, Long ownerId, Long tenantId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.tenantId = tenantId;
    }

    public Resource(String name, String description, Long ownerId, Long tenantId, String resourceType) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.tenantId = tenantId;
        this.resourceType = resourceType;
    }

    public enum AccessLevel {
        PRIVATE("Only accessible by owner"),
        SHARED("Accessible by all users in tenant"),
        PUBLIC("Accessible by all users");

        private final String description;

        AccessLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}