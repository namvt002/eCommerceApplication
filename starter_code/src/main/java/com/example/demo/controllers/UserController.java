package com.example.demo.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Define the UserController class to handle user-related HTTP requests
@RestController
@RequestMapping("/api/user") // Define the base URL for user-related API endpoints
public class UserController {

	// Logger to track activities and errors in this controller
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	// Autowire the UserRepository to interact with the User table in the database
	@Autowired
	private UserRepository userRepository;

	// Autowire the CartRepository to interact with the Cart table in the database
	@Autowired
	private CartRepository cartRepository;

	// Autowire BCryptPasswordEncoder to handle password encryption
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Endpoint to retrieve a user by their ID
	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Finding user by ID: {}", id); // Log the attempt to find a user by ID
		// Return the user if found, otherwise return HTTP 404 Not Found
		return ResponseEntity.of(userRepository.findById(id));
	}

	// Endpoint to retrieve a user by their username
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("Finding user by username: {}", username); // Log the attempt to find a user by username
		// Retrieve the user using the provided username
		User user = userRepository.findByUsername(username);
		// Return the user if found, otherwise return HTTP 404 Not Found
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	// Endpoint to create a new user
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		log.info("Creating user: {}", createUserRequest.getUsername()); // Log the attempt to create a user

		// Create a new User entity
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		// Create a new Cart for the user and save it
		Cart cart = new Cart();
		cartRepository.save(cart);

		// Associate the newly created cart with the user
		user.setCart(cart);

		// Validate the password
		if (createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build(); // Return HTTP 400 Bad Request if password is invalid
		}

		// Encrypt the password using BCrypt
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		// Save the user entity to the database
		userRepository.save(user);

		log.info("User created successfully: {}", user.getUsername()); // Log the successful creation of the user
		// Return the created user with HTTP 200 OK status
		return ResponseEntity.ok(user);
	}
}
