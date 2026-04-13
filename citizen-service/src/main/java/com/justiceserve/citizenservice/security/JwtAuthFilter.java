package com.justiceserve.citizenservice.security;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;

@Slf4j @Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String userId = req.getHeader("X-User-Id");
        String role   = req.getHeader("X-User-Role");
        String email  = req.getHeader("X-User-Email");
        if (StringUtils.hasText(userId) && StringUtils.hasText(role)) {
            var auth = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
