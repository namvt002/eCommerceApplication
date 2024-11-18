package com.example.demo.controllers;

// Import necessary libraries for handling HTTP requests, logging, and managing entities
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

// Mark the class as a REST controller and define the base URL for item-related endpoints
@RestController
@RequestMapping("/api/item")
public class ItemController {

	// Logger to track the flow of requests and debug information for the ItemController class
	private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	// Inject the ItemRepository dependency for interacting with the database
	@Autowired
	private ItemRepository itemRepository;

	// GET endpoint to retrieve all items from the database
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		// Retrieve the list of all items from the database
		List<Item> items = itemRepository.findAll();

		// Log the successful retrieval of items
		logger.info("Retrieval all items");

		// Return the list of items wrapped in a ResponseEntity with HTTP 200 (OK) status
		return ResponseEntity.ok(items);
	}

	// GET endpoint to retrieve a single item by its ID
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		// Log the attempt to retrieve an item by its ID
		logger.info("Retrieval item id: {}", id);

		// Use ResponseEntity.of() to handle the case where the item may or may not be found
		return ResponseEntity.of(itemRepository.findById(id));
	}

	// GET endpoint to retrieve items by their name
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		// Log the attempt to retrieve items by their name
		logger.info("Retrieval items by name: {}", name);

		// Retrieve items from the database by their name
		List<Item> items = itemRepository.findByName(name);

		// If no items are found, return HTTP 404 (Not Found); otherwise, return the items with HTTP 200 (OK) status
		return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(items);
	}
}
