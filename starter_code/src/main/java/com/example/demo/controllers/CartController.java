package com.example.demo.controllers;

// Import necessary libraries for handling HTTP requests, logging, and managing entities
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Marks this class as a Spring REST controller and sets up the base URL for cart-related endpoints
@RestController
@RequestMapping("/api/cart")
public class CartController {

	// Inject the UserRepository, CartRepository, and ItemRepository for interacting with the database
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ItemRepository itemRepository;

	// Logger to log messages for debugging or tracking cart-related activities
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	// POST endpoint to add items to a user's cart
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		// Log the attempt to add an item to the cart
		log.info("Add to Cart with username [{}], item id [{}]", request.getUsername(), request.getItemId());

		// Retrieve the user by username from the database
		User user = userRepository.findByUsername(request.getUsername());

		// If user not found, log the error and return a NOT_FOUND response
		if(user == null) {
			log.error("Cannot add to cart with username [{}], item id [{}]", request.getUsername(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Retrieve the item by its ID from the database
		Optional<Item> item = itemRepository.findById(request.getItemId());

		// If item not found, log the error and return a NOT_FOUND response
		if(!item.isPresent()) {
			log.error("Cannot add to cart with username [{}], item id [{}]", request.getUsername(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Get the user's cart
		Cart cart = user.getCart();

		// Add the item to the cart the specified number of times (based on the quantity in the request)
		IntStream.range(0, request.getQuantity())
				.forEach(i -> cart.addItem(item.get())); // Add item to cart multiple times based on the quantity

		// Save the updated cart back to the database
		cartRepository.save(cart);

		// Log success and return the updated cart as the response
		log.info("Add to Cart success username [{}], id [{}]", request.getUsername(), request.getItemId());
		return ResponseEntity.ok(cart);
	}

	// POST endpoint to remove items from a user's cart
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		// Log the attempt to remove an item from the cart
		log.info("Remove from Cart username [{}], id [{}]", request.getUsername(), request.getItemId());

		// Retrieve the user by username from the database
		User user = userRepository.findByUsername(request.getUsername());

		// If user not found, log the error and return a NOT_FOUND response
		if(user == null) {
			log.error("Cannot remove from cart username [{}], id [{}]", request.getUsername(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Retrieve the item by its ID from the database
		Optional<Item> item = itemRepository.findById(request.getItemId());

		// If item not found, log the error and return a NOT_FOUND response
		if(!item.isPresent()) {
			log.error("Cannot remove from cart username [{}], id [{}]", request.getUsername(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Get the user's cart
		Cart cart = user.getCart();

		// Remove the item from the cart the specified number of times (based on the quantity in the request)
		IntStream.range(0, request.getQuantity())
				.forEach(i -> cart.removeItem(item.get())); // Remove item from cart multiple times based on the quantity

		// Save the updated cart back to the database
		cartRepository.save(cart);

		// Log success and return the updated cart as the response
		log.info("Remove from Cart username [{}], id [{}]", request.getUsername(), request.getItemId());
		return ResponseEntity.ok(cart);
	}
}
