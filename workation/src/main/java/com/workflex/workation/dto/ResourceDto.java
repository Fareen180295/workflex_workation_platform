package com.workflex.workation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResourceDto {

    private Long id;

    @NotBlank(message = "Resource name cannot be blank")
    private String name;

    private String description;

    private Long ownerId;
    private String ownerUsername;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for response DTOs
    public ResourceDto(Long id, String name, String description, Long ownerId, 
                      String ownerUsername, Long tenantId, String tenantName,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}