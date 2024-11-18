package com.example.demo.security;

// Import necessary libraries for JWT handling, Spring Security authentication, and object mapping
import com.auth0.jwt.JWT;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

// Static imports for JWT signing algorithm and constant values
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.example.demo.util.Constant.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // Declare an AuthenticationManager to handle the authentication process
    private final AuthenticationManager authenticationManager;

    // Constructor to inject the AuthenticationManager dependency
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // Override the attemptAuthentication method to process the incoming authentication request
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            // Read the incoming JSON request body and map it to a User object
            User credentials = new ObjectMapper().readValue(req.getInputStream(), User.class);

            // Create an authentication token using the provided username and password from the request
            // The empty list here represents no authorities (roles) being assigned in this context
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            Collections.emptyList())
            );
        } catch (IOException e) {
            // If there's an error during JSON deserialization, throw a RuntimeException
            throw new RuntimeException(e);
        }
    }

    // Override the successfulAuthentication method to generate a JWT token upon successful login
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // Create a JWT token using the auth object (which contains the authenticated user's details)
        // The subject of the JWT will be the username of the authenticated user
        String token = JWT.create()
                .withSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())  // Set the subject (username) in the token
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Set the expiration time of the token
                .sign(HMAC512(SECRET.getBytes()));  // Sign the token using the HMAC512 algorithm with the secret key

        // Add the generated JWT token to the response header with the appropriate prefix
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
