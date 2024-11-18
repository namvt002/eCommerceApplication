package com.example.demo.controllers;

// Import necessary libraries for handling HTTP requests, logging, and managing entities
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Define the REST controller for handling orders with base URL /api/order
@RestController
@RequestMapping("/api/order")
public class OrderController {

	// Logger to track the flow of requests and debug information for the OrderController class
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	// Autowiring dependencies for repositories to interact with the database
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	// POST endpoint for submitting an order. The user is identified by the username in the URL
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		// Log the attempt to submit an order for the given username
		logger.info("Attempt to submit order for user: {}", username);

		// Retrieve the user from the database using the username
		User user = userRepository.findByUsername(username);
		if (user == null) {
			// If the user is not found, log the error and return HTTP 404 Not Found
			logger.warn("User not found: {}", username);
			return ResponseEntity.notFound().build();
		}

		// Create a UserOrder object from the user's cart
		UserOrder order = UserOrder.createFromCart(user.getCart());

		// Save the order to the database
		orderRepository.save(order);

		// Log the successful submission of the order
		logger.info("Order submit successfully for user: {}", username);

		// Return the created order in the response body with HTTP 200 OK status
		return ResponseEntity.ok(order);
	}

	// GET endpoint to retrieve the order history of a user by username
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		// Log the attempt to retrieve the order history for the given username
		logger.info("Attempt to retrieve order history for user: {}", username);

		// Retrieve the user from the database using the username
		User user = userRepository.findByUsername(username);
		if (user == null) {
			// If the user is not found, log the error and return HTTP 404 Not Found
			logger.warn("User not found: {}", username);
			return ResponseEntity.notFound().build();
		}

		// Log the successful retrieval of the user's order history
		logger.info("Order history retrieval successfully for user: {}", username);

		// Return the list of orders associated with the user, with HTTP 200 OK status
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
