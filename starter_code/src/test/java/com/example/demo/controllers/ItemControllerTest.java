package com.example.demo.controllers;

// Import necessary classes and packages
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ItemControllerTest {

    // Declare an ItemController instance and a mock ItemRepository
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setUpMocked() {
        // Initialize the ItemController instance
        itemController = new ItemController();
        // Inject the mock itemRepository into the itemController
        injectDependenciesMocked(itemController, itemRepository);
    }


    private static void injectDependenciesMocked(Object target, Object toInject) {
        try {
            // Get the private field 'itemRepository' from the target (ItemController)
            Field field = target.getClass().getDeclaredField("itemRepository");
            boolean accessible = field.isAccessible();
            field.setAccessible(true);  // Make the field accessible if it's private
            field.set(target, toInject);  // Inject the mock repository
            field.setAccessible(accessible);  // Restore the field's original accessibility
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e); // If there's an error during reflection, throw a runtime exception
        }
    }

    @Test
    public void getItemByNameEmpty() throws Exception {
        // Mock the repository method to return null when findByName() is called with "testItem"
        when(itemRepository.findByName("testItem")).thenReturn(null);

        // Call the controller's method to get items by name
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");

        // Assert the response is not null and the status code is 404 (Not Found) since no items are found
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }


    @Test
    public void getAllItem() throws Exception {
        // Create a test Item object
        Item item = new Item();
        item.setName("ItemTest");
        item.setPrice(new BigDecimal("6.99"));
        item.setDescription("testItem description");

        // Create a list of expected items with different IDs
        List<Item> expectedItems = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            item.setId((long) i);
            expectedItems.add(item); // Add the item to the expected items list
        }

        // Mock the repository method to return the expected items when findAll() is called
        when(itemRepository.findAll()).thenReturn(expectedItems);

        // Call the controller's method to get all items
        final ResponseEntity<List<Item>> response = itemController.getItems();

        // Assert the response is not null and the status code is 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the actual list of items from the response body
        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);  // Assert the actual items are not null
        assertEquals(expectedItems, actualItems);  // Assert the expected and actual items match
    }

    @Test
    public void getItemById() throws Exception {
        // Create a test Item object with ID 0
        Item item = new Item();
        item.setId((long) 0);
        item.setName("ItemTest");
        item.setPrice(new BigDecimal("6.99"));
        item.setDescription("TestItem description");

        // Mock the repository method to return the item when findById() is called with ID 0
        when(itemRepository.findById((long) 0)).thenReturn(java.util.Optional.of(item));

        // Call the controller's method to get the item by ID
        final ResponseEntity<Item> response = itemController.getItemById((long) 0);

        // Assert the response is not null and the status code is 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the actual item from the response body
        Item actualItem = response.getBody();
        assertNotNull(actualItem);  // Assert the actual item is not null

        // Assert that the ID, name, price, and description of the actual item match the expected item
        assertEquals(item.getId(), actualItem.getId());
        assertEquals(item.getName(), actualItem.getName());
        assertEquals(item.getPrice(), actualItem.getPrice());
        assertEquals(item.getDescription(), actualItem.getDescription());
    }

    @Test
    public void getItemByName() throws Exception {
        // Create a test Item object
        Item item = new Item();
        item.setName("ItemTest");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("This is a testItem description");

        // Create a list of expected items with different IDs
        List<Item> expectedItems = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            item.setId((long) i);
            expectedItems.add(item); // Add the item to the expected items list
        }

        // Mock the repository method to return the expected items when findByName() is called with "testItem"
        when(itemRepository.findByName("ItemTest")).thenReturn(expectedItems);

        // Call the controller's method to get items by name
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("ItemTest");

        // Assert the response is not null and the status code is 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the actual list of items from the response body
        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);  // Assert the actual items are not null
        assertEquals(expectedItems, actualItems);  // Assert the expected and actual items match
    }
}
