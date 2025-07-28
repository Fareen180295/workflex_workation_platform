package com.workflex.workation.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@Slf4j
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    private static final String DEFAULT_TENANT_ID = "public";
    private static final String TENANT_HEADER_NAME = "X-Tenant-ID";

    @Override
    public String resolveCurrentTenantIdentifier() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String tenantId = request.getHeader(TENANT_HEADER_NAME);
                
                if (tenantId != null && !tenantId.trim().isEmpty()) {
                    log.debug("Resolved tenant ID: {}", tenantId);
                    return sanitizeTenantId(tenantId);
                }
                
                // Try to get from request attribute (set by JWT filter)
                Object tenantFromAuth = request.getAttribute("tenantId");
                if (tenantFromAuth != null) {
                    String authTenantId = tenantFromAuth.toString();
                    log.debug("Resolved tenant ID from auth: {}", authTenantId);
                    return sanitizeTenantId(authTenantId);
                }
            }
        } catch (Exception e) {
            log.warn("Error resolving tenant identifier, using default: {}", e.getMessage());
        }
        
        log.debug("Using default tenant ID: {}", DEFAULT_TENANT_ID);
        return DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    private String sanitizeTenantId(String tenantId) {
        // Sanitize tenant ID to prevent SQL injection and ensure valid schema names
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return DEFAULT_TENANT_ID;
        }
        
        // Remove any characters that are not alphanumeric or underscore
        String sanitized = tenantId.trim().replaceAll("[^a-zA-Z0-9_]", "");
        
        // Ensure it starts with a letter
        if (sanitized.isEmpty() || !Character.isLetter(sanitized.charAt(0))) {
            return DEFAULT_TENANT_ID;
        }
        
        // Limit length to 63 characters (PostgreSQL identifier limit)
        if (sanitized.length() > 63) {
            sanitized = sanitized.substring(0, 63);
        }
        
        return sanitized.toLowerCase();
    }

    /**
     * Manually set the tenant ID (useful for system operations)
     */
    public static void setCurrentTenantId(String tenantId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("tenantId", tenantId);
        }
    }

    /**
     * Clear the current tenant ID
     */
    public static void clearCurrentTenantId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.removeAttribute("tenantId");
        }
    }
}