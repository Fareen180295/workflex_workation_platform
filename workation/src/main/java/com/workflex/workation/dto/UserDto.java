package com.workflex.workation.dto;

import com.workflex.workation.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    private String password; // Only used for creation/update

    @NotNull(message = "Role cannot be null")
    private User.UserRole role;

    private Long tenantId;
    private String tenantName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor without password for response DTOs
    public UserDto(Long id, String username, User.UserRole role, Long tenantId, String tenantName, 
                   Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}