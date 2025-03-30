package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in development
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/*.js", "/*.css", "/static/**").permitAll() // Allow access to root and static resources
                .requestMatchers("/api/**").permitAll() // Allow all API requests without authentication for development
                .requestMatchers("/h2-console/**").permitAll() // Allow access to H2 console
                .anyRequest().permitAll() // Allow all requests for now since we don't have authentication set up
            )
            .headers(headers -> headers.frameOptions().disable()); // Allow H2 console to be displayed in an iframe

        return http.build();
    }
}