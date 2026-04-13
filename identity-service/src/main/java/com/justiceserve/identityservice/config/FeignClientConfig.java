package com.justiceserve.identityservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * FeignClientInterceptor — forwards the JWT from the current request
 * to all outgoing Feign calls made by this service.
 * <p>
 * WHY: When case-service calls citizen-service via Feign,
 * citizen-service's JwtAuthFilter requires a valid JWT.
 * Without this interceptor, the Feign call has no Authorization header
 * and would be rejected as unauthenticated.
 * <p>
 * HOW: Reads the Authorization header from the incoming HTTP request
 * (put there by the API Gateway via the original client's JWT)
 * and forwards it to every outgoing Feign call automatically.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor jwtForwardingInterceptor() {
        return new JwtForwardingInterceptor();
    }

    public static class JwtForwardingInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            // Get the current incoming HTTP request
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes attrs) {
                HttpServletRequest request = attrs.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // Forward the same JWT to the downstream service
                    template.header("Authorization", authHeader);
                }
            }
        }
    }
}
