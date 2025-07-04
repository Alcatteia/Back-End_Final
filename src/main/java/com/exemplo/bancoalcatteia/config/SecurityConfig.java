package com.exemplo.bancoalcatteia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionAuthenticationFilter sessionAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/usuarios/login", "/usuarios/criar", "/usuarios/logout").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Endpoints que requerem autenticação específica
                .requestMatchers("/api/admin/**").hasRole("RH")
                .requestMatchers("/api/kanban/**").hasAnyRole("RH", "LIDER", "FUNC")
                .requestMatchers("/api/humor/**").hasAnyRole("RH", "LIDER", "FUNC")
                .requestMatchers("/api/relatorios/**").hasAnyRole("RH", "LIDER")
                .requestMatchers("/api/dashboard/**").hasAnyRole("RH", "LIDER")
                
                // Todos os outros endpoints requerem autenticação
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Para H2 Console
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 
