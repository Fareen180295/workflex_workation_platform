package com.workflex.workation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {

    private Long id;

    @NotBlank(message = "Tenant name cannot be blank")
    private String name;

    @NotBlank(message = "Schema name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "Schema name must start with a letter and contain only letters, numbers, and underscores")
    private String schemaName;

    private Integer maxUsers = 50;
    private Integer maxResources = 500;
    private String contactEmail;
    private String subscriptionPlan;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creation
    public TenantDto(String name, String schemaName) {
        this.name = name;
        this.schemaName = schemaName;
    }

    public TenantDto(String name, String schemaName, String contactEmail) {
        this.name = name;
        this.schemaName = schemaName;
        this.contactEmail = contactEmail;
    }
}