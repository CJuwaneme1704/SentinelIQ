package com.sentineliq.backend.config;

// Import necessary Spring Security and configuration classes
import org.springframework.context.annotation.Bean; // Marks a method as a Spring-managed bean
import org.springframework.context.annotation.Configuration; // Marks this class as a configuration class
import org.springframework.security.authentication.AuthenticationManager; // Manages authentication processes
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Provides the authentication manager configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Enables Spring Security for the application
import org.springframework.security.config.http.SessionCreationPolicy; // Configures session management policies
import org.springframework.security.web.SecurityFilterChain; // Configures the security filter chain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Matches specific request paths
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Configures HTTP security
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Adds custom filters to the security chain
import org.springframework.beans.factory.annotation.Autowired; // Injects dependencies into the class
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Provides password hashing using BCrypt
import org.springframework.security.crypto.password.PasswordEncoder; // Interface for password encoding

/**
 * Security configuration class for the application.
 * Configures authentication, authorization, and security filters.
 */
@Configuration // Marks this class as a Spring configuration class
@EnableWebSecurity // Enables Spring Security for the application
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; 
    // Injects a custom JWT authentication filter to validate JWT tokens in requests

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; 
    // Injects a custom entry point to handle unauthorized access attempts

    /**
     * Configures the security filter chain for the application.
     * Defines how requests are authenticated and authorized.
     *
     * @param http The HttpSecurity object used to configure security
     * @return A SecurityFilterChain object
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            // Disables CSRF protection (useful for stateless APIs)

            .cors(cors -> {}) 
            // Enables Cross-Origin Resource Sharing (CORS) with default settings

            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)) 
            // Configures the custom entry point to handle unauthorized access attempts

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            // Configures the application to use stateless sessions (no server-side session storage)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/api/signup"), 
                    new AntPathRequestMatcher("/api/login")
                ).permitAll() 
                // Allows unauthenticated access to the `/api/signup` and `/api/login` endpoints

                .anyRequest().authenticated() 
                // Requires authentication for all other requests
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 
            // Adds the custom JWT authentication filter before the default username/password authentication filter

        return http.build(); 
        // Builds and returns the configured SecurityFilterChain
    }

    /**
     * Provides the AuthenticationManager bean.
     * Used to authenticate users during login.
     *
     * @param config The AuthenticationConfiguration object
     * @return An AuthenticationManager object
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); 
        // Retrieves the default AuthenticationManager from the configuration
    }

    /**
     * Provides the PasswordEncoder bean.
     * Used to hash passwords securely using BCrypt.
     *
     * @return A PasswordEncoder object
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
        // Returns a BCryptPasswordEncoder instance for password hashing
    }
}
