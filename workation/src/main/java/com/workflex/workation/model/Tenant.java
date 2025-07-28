package com.workflex.workation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tenant name cannot be blank")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Schema name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "Schema name must start with a letter and contain only letters, numbers, and underscores")
    @Column(name = "schema_name", unique = true, nullable = false)
    private String schemaName;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers = 50;

    @Column(name = "max_resources", nullable = false)
    private Integer maxResources = 500;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Additional fields for tenant management
    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    public Tenant(String name, String schemaName) {
        this.name = name;
        this.schemaName = schemaName;
    }

    public Tenant(String name, String schemaName, String contactEmail) {
        this.name = name;
        this.schemaName = schemaName;
        this.contactEmail = contactEmail;
    }
}