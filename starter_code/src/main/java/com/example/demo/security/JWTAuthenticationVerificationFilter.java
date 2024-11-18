package com.example.demo.security;

// Import necessary libraries for JWT handling, Spring Security, logging, and servlet processing
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// Static imports for constants like token header and prefix
import static com.example.demo.util.Constant.*;

@Component  // Mark this class as a Spring Component, so it can be autowired into other parts of the application
public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    // Logger to log messages for debugging or tracking token verification process
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationVerificationFilter.class);

    // Constructor that takes AuthenticationManager as a parameter and passes it to the superclass constructor
    public JWTAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    // The doFilterInternal method is called for every HTTP request to filter incoming requests
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        // Retrieve the Authorization header from the request (where the JWT is usually sent)
        String header = req.getHeader(HEADER_STRING);
        logger.debug("Header: {}", header);

        // If the header is missing or doesn't start with the correct prefix (e.g., "Bearer "), the token is invalid
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            logger.warn("Invalid header or token prefix.");
            // If no valid token, continue the filter chain without performing authentication
            chain.doFilter(req, res);
            return;
        }

        // If the token is valid, get the authentication token from the request
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        // Set the authentication object in the SecurityContext so that it can be used throughout the request
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue with the filter chain to let other filters process the request
        chain.doFilter(req, res);
    }

    // This method extracts the JWT from the request, validates it, and returns an Authentication token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Get the token from the Authorization header
        String token = request.getHeader(HEADER_STRING);
        logger.debug("Token: {}", token);

        if (token != null) {
            // Remove the "Bearer " prefix from the token and verify it using the secret key
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))  // Use HMAC512 algorithm with the secret key
                    .build()  // Build the JWT verifier
                    .verify(token.replace(TOKEN_PREFIX, ""))  // Verify and decode the token
                    .getSubject();  // Extract the subject (username) from the decoded token

            logger.debug("User: {}", user);

            // If the username (subject) is found, return a UsernamePasswordAuthenticationToken to authenticate the user
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            // If no user is found (invalid token), return null
            return null;
        }
        // If no token is provided, return null
        return null;
    }
}
