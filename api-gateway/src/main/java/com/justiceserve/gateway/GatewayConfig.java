package com.justiceserve.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("identity-auth", r -> r.path("/api/auth/**").uri("lb://identity-service"))
                .route("identity-users", r -> r.path("/api/users/**").uri("lb://identity-service"))
                .route("citizen", r -> r.path("/api/citizens/**").uri("lb://citizen-service"))
                .route("case", r -> r.path("/api/cases/**", "/api/files/**").uri("lb://case-service"))
                .route("hearing", r -> r.path("/api/hearings/**").uri("lb://hearing-service"))
                .route("judgment", r -> r.path("/api/judgments/**", "/api/court-orders/**").uri("lb://judgment-service"))
                .route("compliance", r -> r.path("/api/compliance/**", "/api/audits/**", "/api/audit-logs/**").uri("lb://compliance-service"))
                .route("report", r -> r.path("/api/reports/**").uri("lb://report-service"))
                .route("notification", r -> r.path("/api/notifications/**").uri("lb://notification-service"))
                .build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration cfg = new CorsConfiguration();
//        // Use pattern matching for flexibility (handles trailing slashes etc.)
//        cfg.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
//        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
//        cfg.setAllowedHeaders(Arrays.asList("*"));
//        cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-User-Id", "X-User-Role", "X-User-Email"));
//        cfg.setAllowCredentials(true);
//        cfg.setMaxAge(3600L); // cache preflight for 1 hour
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", cfg);
//        return source;
//    }
//
//    @Bean
//    public org.springframework.web.server.WebFilter corsWebFilter() {
//        return (exchange, chain) -> {
//            org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest();
//            if (org.springframework.http.HttpMethod.OPTIONS.equals(request.getMethod())) {
//                org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
//                response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
//                response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
//                response.getHeaders().add("Access-Control-Allow-Headers", "*");
//                response.getHeaders().add("Access-Control-Allow-Credentials", "true");
//                response.getHeaders().add("Access-Control-Max-Age", "3600");
//                response.setStatusCode(org.springframework.http.HttpStatus.OK);
//                return response.setComplete();
//            }
//            return chain.filter(exchange);
//        };
//    }


}