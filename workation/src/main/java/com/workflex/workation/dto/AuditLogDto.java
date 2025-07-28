package com.workflex.workation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {

    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String details;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime timestamp;
}