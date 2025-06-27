package com.lecturebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // Import CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource; // Import CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Import UrlBasedCorsConfigurationSource

import com.lecturebot.repository.UserRepository;
import com.lecturebot.security.JwtAuthenticationFilter;
import jakarta.annotation.PostConstruct;

import java.util.Arrays; // Import Arrays
import java.util.List; // Import List

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${LECTUREBOT_CLIENT_ORIGIN}")
    private String clientOrigin;

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use
                                                                                                              // stateless
                                                                                                              // sessions
                                                                                                              // for JWT
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight
                        // requests
                        .requestMatchers("/auth/**", "/health").permitAll() // Permit auth and health endpoints
                        .anyRequest().permitAll()// .authenticated() // Secure all other endpoints
                );

        // Add the JWT filter before the standard username/password authentication
        // filter
        // http.addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);

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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPasswordHash(),
                        // Add authorities/roles here if you have them
                        List.of()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Bean // Bean for CORS configuration
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
