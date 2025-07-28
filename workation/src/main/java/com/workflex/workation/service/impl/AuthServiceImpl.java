package com.workflex.workation.service.impl;

import com.workflex.workation.config.JwtTokenProvider;
import com.workflex.workation.dto.AuthRequest;
import com.workflex.workation.dto.AuthResponse;
import com.workflex.workation.exception.ResourceNotFoundException;
import com.workflex.workation.model.User;
import com.workflex.workation.repository.UserRepository;
import com.workflex.workation.service.AuthService;
import com.workflex.workation.service.AuditService;
import com.workflex.workation.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TenantService tenantService;
    private final AuditService auditService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Authenticating user: {} for tenant: {}", authRequest.getUsername(), authRequest.getTenantId());
        
        Long tenantId = Long.parseLong(authRequest.getTenantId());
        
        // Verify tenant exists and is active
        if (!tenantService.tenantExistsById(tenantId)) {
            throw new ResourceNotFoundException("Tenant not found: " + tenantId);
        }
        
        // Find user by username and tenant
        User user = userRepository.findByUsernameAndTenantId(authRequest.getUsername(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authRequest.getUsername()));
        
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User account is deactivated");
        }
        
        // Verify password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Create authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());
        
        // Generate JWT token
        String token = tokenProvider.generateTokenFromUsername(
                user.getUsername(), tenantId, user.getRole().name());
        
        // Log authentication
        auditService.logUserLogin(user.getId(), tenantId);
        
        log.info("User authenticated successfully: {}", authRequest.getUsername());
        
        String tenantName = tenantService.getTenantById(tenantId).getName();
        
        return new AuthResponse(
                token, 
                user.getId(), 
                user.getUsername(), 
                user.getRole(), 
                tenantId,
                tenantName,
                tokenProvider.getExpirationTime()
        );
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token) && !tokenProvider.isTokenExpired(token);
    }

    @Override
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        String tenantId = getCurrentTenantId().toString();
        
        User user = userRepository.findByUsernameAndTenantId(username, Long.parseLong(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        return user.getId();
    }

    @Override
    public Long getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() != null) {
            // Extract tenant ID from authentication details or token
            // This is a simplified implementation
            return 1L; // Default tenant - in real implementation, extract from JWT
        }
        throw new IllegalStateException("No authenticated user found");
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    @Override
    public void logout(String token) {
        // In a real implementation, you would add the token to a blacklist
        // For now, we'll just log the logout
        String username = tokenProvider.getUsernameFromToken(token);
        String tenantId = tokenProvider.getTenantIdFromToken(token);
        
        if (username != null && tenantId != null) {
            User user = userRepository.findByUsernameAndTenantId(username, Long.parseLong(tenantId))
                    .orElse(null);
            if (user != null) {
                auditService.logUserLogout(user.getId(), Long.parseLong(tenantId));
                log.info("User logged out: {}", username);
            }
        }
    }
}