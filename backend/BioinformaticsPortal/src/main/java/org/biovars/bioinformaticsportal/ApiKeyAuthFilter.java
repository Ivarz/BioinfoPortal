package org.biovars.bioinformaticsportal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${apiKey}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader("X-API-KEY");  // API key is sent in request header

        if (apiKey.equals(requestApiKey)) {
            filterChain.doFilter(request, response);  // Valid API key, proceed with the request
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // Invalid API key, reject the request
            response.getWriter().write("Unauthorized: Invalid API Key");
        }
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api");
    }
}
