package com.workflex.workation.dto;

import com.workflex.workation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private User.UserRole role;
    private Long tenantId;
    private String tenantName;
    private Long expiresIn;

    public AuthResponse(String token, Long userId, String username, User.UserRole role, 
                       Long tenantId, String tenantName, Long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.expiresIn = expiresIn;
    }
}