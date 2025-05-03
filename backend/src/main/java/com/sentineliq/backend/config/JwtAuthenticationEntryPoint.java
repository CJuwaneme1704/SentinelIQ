package com.sentineliq.backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component // So Spring can auto-detect it and use it as a bean
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 🔐 Called whenever an unauthenticated user tries to access a protected resource
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 🌐 Respond with 401 Unauthorized and a simple message
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing token.");
    }
}
