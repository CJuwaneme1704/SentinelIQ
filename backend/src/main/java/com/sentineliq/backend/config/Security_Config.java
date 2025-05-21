package com.sentineliq.backend.config;

import com.sentineliq.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Security_Config {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ✅ Constructor-based injection of the JWT filter
    public Security_Config(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API use
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/signup",
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/logout",
                    "/api/auth/check"
                ).permitAll() // Allow public access to auth endpoints
                .anyRequest().authenticated() // Require auth for all other requests
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .deleteCookies("access_token", "refresh_token")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            // ✅ Add custom JWT auth filter before default username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
