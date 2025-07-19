package com.lecturebot.config;

import java.util.Arrays; // Import Arrays
import java.util.List; // Import List

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.web.cors.CorsConfiguration; // Import CorsConfiguration
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import com.lecturebot.security.JwtAuthenticationFilter;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${LECTUREBOT_CLIENT_ORIGIN}")
    private String clientOrigin;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for reactive applications
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Use reactive CSRF disabling
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/v1/v3/api-docs/**",
                                "/api/v1/swagger-ui/**",
                                "/api/v1/swagger-ui.html",
                                "/api/v1/actuator/**",
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()
                        .pathMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/health",
                                "/api/v1/genai/**",
                                "/eureka/**",
                                "/api/v1/eureka/**")
                        .permitAll()
                        .anyExchange().authenticated() // Use authorizeExchange and pathMatchers
                )
                // Add the filter using the reactive approach
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @PostConstruct
    public void logOrigin() {
        System.out.println("Resolved CORS origin: " + clientOrigin);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Specify allowed origins. For development, you can use the specific frontend
        // URL.
        // For production, list your actual frontend domain(s).
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173", // For Vite dev server
                "http://localhost:3000", // For Dockerized client on port 3000
                "http://localhost:8080", // For Dockerized client on port 3000
                clientOrigin // to allow client requests in cluster environment
        // "https://team-lecture-bot.student.k8s.aet.cit.tum.de" // test hardcoded value
        // for production
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
                "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true); // Important if you plan to use cookies or sessions
        configuration.setMaxAge(3600L); // How long the results of a preflight request can be cached

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }
}
