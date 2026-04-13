package com.justiceserve.notificationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

/**
 * JwtAuthFilter — validates JWT on EVERY request hitting this service directly.
 * <p>
 * WHY: Even if request comes through API Gateway (which also validates JWT),
 * a bad actor could call this service directly with fake X-User-* headers
 * and bypass security. This filter validates the JWT signature itself
 * as a second layer of defense.
 * <p>
 * Flow:
 * 1. Read Authorization: Bearer <token> header
 * 2. Validate JWT signature with the shared secret
 * 3. Extract userId, role, email from JWT claims
 * 4. Set Spring SecurityContext so @PreAuthorize works
 * <p>
 * Internal Feign calls (audit-logs/internal, notifications/internal) bypass this.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Skip JWT validation for public paths
        String path = request.getRequestURI();
        boolean isPublic = path.startsWith("/actuator");

        isPublic = isPublic || path.startsWith("/api/audit-logs/internal");
        isPublic = isPublic || path.startsWith("/api/notifications/internal");

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token — path={}", request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            String userId = String.valueOf(claims.get("userId"));
            String role = String.valueOf(claims.get("role"));
            String email = claims.getSubject();

            if (StringUtils.hasText(userId) && StringUtils.hasText(role)) {
                var authority = new SimpleGrantedAuthority("ROLE_" + role);
                var auth = new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("JWT validated — userId={}, role={}, path={}", userId, role, request.getRequestURI());
            }
        } catch (Exception e) {
            log.warn("JWT validation failed for path={}: {}", request.getRequestURI(), e.getMessage());
            // Don't set authentication — request will fail @PreAuthorize checks
        }

        chain.doFilter(request, response);
    }
}
