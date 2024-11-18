package com.example.demo.service;

// Import necessary classes for handling user details and security
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Static import to handle an empty list for granted authorities
import static java.util.Collections.emptyList;

// This annotation indicates that this class is a Spring service component
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Logger to log messages for debugging or tracking user loading process
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    // Automatically inject the UserRepository to interact with the database
    @Autowired
    private UserRepository userRepository;

    // Override the loadUserByUsername method from UserDetailsService interface
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log an info message when attempting to load a user by username
        logger.info("Attempting to load user by username: {}", username);

        // Find the user from the database using the injected UserRepository
        User user = userRepository.findByUsername(username);

        // If the user is not found, log the error and throw an exception
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new UsernameNotFoundException(username);  // Throw exception if user does not exist
        }

        // Log that the user was found successfully
        logger.info("User found: {}", username);

        // Return a UserDetails object (Spring Security's implementation) for the found user
        // We use an empty list for granted authorities (roles) as they are not defined here
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), emptyList());
    }
}
