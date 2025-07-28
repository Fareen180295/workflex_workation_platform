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
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_tenant_username", columnList = "tenant_id, username", unique = true),
    @Index(name = "idx_user_tenant", columnList = "tenant_id")
})
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @NotNull(message = "Tenant ID cannot be null")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

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

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // One-to-Many relationship with resources
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resource> resources;

    public User(String username, String password, UserRole role, Long tenantId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.tenantId = tenantId;
    }

    public User(String username, String password, UserRole role, Long tenantId, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.tenantId = tenantId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public enum UserRole {
        ADMIN("Can manage users and resources within their tenant"),
        MANAGER("Can manage resources but cannot manage users"),
        EMPLOYEE("Can view resources only");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}