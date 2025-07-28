package com.workflex.workation.service;

import com.workflex.workation.dto.AuthRequest;
import com.workflex.workation.dto.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest authRequest);

    boolean validateToken(String token);

    Long getCurrentUserId();

    Long getCurrentTenantId();

    String getCurrentUsername();

    String getCurrentUserRole();

    void logout(String token);
}