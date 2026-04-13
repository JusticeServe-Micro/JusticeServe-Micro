package com.justiceserve.caseservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
