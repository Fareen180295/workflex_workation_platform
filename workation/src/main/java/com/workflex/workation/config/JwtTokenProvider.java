package com.workflex.workation.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey jwtSecret;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, 
                           @Value("${jwt.expiration}") int jwtExpirationInMs) {
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(Authentication authentication, Long tenantId, String role) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername(), tenantId, role);
    }

    public String generateTokenFromUsername(String username, Long tenantId, String role) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("role", role);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }

    public String getTenantIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("tenantId", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting tenant ID from token", e);
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting role from token", e);
            return null;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting expiration date from token", e);
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting claims from token", e);
            return null;
        }
    }

    public long getExpirationTime() {
        return jwtExpirationInMs;
    }
}