package com.justiceserve.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // 2.1 Identity — auth + users
                .route("identity-auth", r -> r.path("/api/auth/**").uri("lb://identity-service"))
                .route("identity-users", r -> r.path("/api/users/**").uri("lb://identity-service"))
                // 2.2 Citizen Registration
                .route("citizen", r -> r.path("/api/citizens/**").uri("lb://citizen-service"))
                // 2.3 Case Filing
                .route("case", r -> r.path("/api/cases/**", "/api/files/**").uri("lb://case-service"))
                // 2.4 Hearing
                .route("hearing", r -> r.path("/api/hearings/**").uri("lb://hearing-service"))
                // 2.5 Judgment & Orders
                .route("judgment", r -> r.path("/api/judgments/**", "/api/court-orders/**").uri("lb://judgment-service"))
                // 2.6 Compliance & Audit
                .route("compliance", r -> r.path("/api/compliance/**", "/api/audits/**", "/api/audit-logs/**").uri("lb://compliance-service"))
                // 2.7 Reports
                .route("report", r -> r.path("/api/reports/**").uri("lb://report-service"))
                // 2.8 Notifications
                .route("notification", r -> r.path("/api/notifications/**").uri("lb://notification-service"))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
