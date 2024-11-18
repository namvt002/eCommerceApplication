package com.example.demo.controllers;

// Importing necessary classes and packages
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    // Declaring fields for the controller and repository mocks
    private CartController cartController;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    // Set up method to initialize the test environment
    @Before
    public void setUpMocked() {
        // Mocking the repositories
        userRepository = mock(UserRepository.class);
        CartRepository cartRepository = mock(CartRepository.class); // Mock cart repository
        itemRepository = mock(ItemRepository.class); // Mock item repository

        // Creating a new instance of CartController
        cartController = new CartController();
        // Injecting the mocked repositories into the controller
        injectMocked(cartController, "userRepository", userRepository);
        injectMocked(cartController, "cartRepository", cartRepository);
        injectMocked(cartController, "itemRepository", itemRepository);
    }

    // Utility method to injectMocked dependencies into a controller using reflection
    private static void injectMocked(Object target, String fieldName, Object toInject) {
        try {
            // Get the field to injectMocked into
            Field field = target.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true); // Make the field accessible
            field.set(target, toInject); // Inject the dependency
            field.setAccessible(accessible); // Restore field's accessibility
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e); // If there's an error, throw a runtime exception
        }
    }

    // Helper method to create a test user with an associated cart
    private User createUserWithCart() {
        User user = new User();
        user.setUsername("test");
        user.setId(0);
        Cart cart = new Cart();
        cart.setId((long) 0);
        cart.setUser(user); // Set the user for the cart
        user.setCart(cart); // Set the cart for the user
        return user; // Return the user with the cart
    }

    // Helper method to create a test item with a specified price
    private Item createItemMocked(BigDecimal price) {
        Item item = new Item();
        item.setId((long) 0);
        item.setName("testItem");
        item.setPrice(price); // Set the price of the item
        item.setDescription("This is a testItem description"); // Set the description
        return item; // Return the item
    }

    // Test case for adding an item to the cart
    @Test
    public void addCart() {
        User user = createUserWithCart(); // Create a test user with a cart
        Item item = createItemMocked(new BigDecimal("2.99")); // Create a test item with price 2.99

        // Create a ModifyCartRequest to modify the cart
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(0);
        modifyCartRequest.setQuantity(3);

        // Mock the responses of the repositories
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        // Call the controller method to add the item to the cart
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assert the response is not null and has a status of 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the actual cart from the response body
        Cart actualCart = response.getBody();
        assertNotNull(actualCart); // Assert the actual cart is not null
        assertEquals(user.getCart().getId(), actualCart.getId()); // Assert cart ID is the same

        // Create the expected list of items in the cart based on the quantity
        List<Item> expectedItems = Collections.nCopies(modifyCartRequest.getQuantity(), item);
        assertEquals(expectedItems, actualCart.getItems()); // Assert the items in the cart match

        assertEquals(user, actualCart.getUser()); // Assert the user in the cart matches
        assertEquals(new BigDecimal("8.97"), actualCart.getTotal()); // Assert the total price is correct
    }

    // Test case for adding an item to the cart when the user is not found
    @Test
    public void addCartNotFound() {
        // Create a ModifyCartRequest with the username and item details
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(0);
        modifyCartRequest.setQuantity(3);

        // Mock the userRepository to return null (user not found)
        when(userRepository.findByUsername("test")).thenReturn(null);

        // Call the controller method to add the item to the cart
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assert the response is not null and has a status of 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // Test case for adding an item to the cart when the item is not found
    @Test
    public void addCartItemNotFound() {
        User user = createUserWithCart(); // Create a test user with a cart

        // Create a ModifyCartRequest for an item that doesn't exist
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(3);

        // Mock the userRepository to return the user and the itemRepository to return empty (item not found)
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the controller method to add the item to the cart
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assert the response is not null and has a status of 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // Test case for removing an item from the cart
    @Test
    public void removeCartMocked() {
        User user = createUserWithCart(); // Create a test user with a cart
        Item item = createItemMocked(BigDecimal.valueOf(2.99)); // Create a test item with price 2.99

        // Create a ModifyCartRequest to remove the item from the cart
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(0);
        modifyCartRequest.setQuantity(1);

        // Add 3 items to the user's cart (so we can remove one)
        List<Item> itemsArray = new ArrayList<>(Collections.nCopies(3, item));
        user.getCart().setItems(itemsArray);
        user.getCart().setTotal(BigDecimal.valueOf(8.97)); // Total price for 3 items

        // Mock the userRepository and itemRepository
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        // Call the controller method to remove the item from the cart
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assert the response is not null and has a status of 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the actual cart from the response body
        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals(user.getCart().getId(), actualCart.getId()); // Assert the cart ID is the same

        // Create the expected list of items after removing one
        List<Item> expectedItemsArray = new ArrayList<>(Collections.nCopies(2, item));
        assertEquals(expectedItemsArray, actualCart.getItems()); // Assert the items in the cart match

        assertEquals(user, actualCart.getUser()); // Assert the user in the cart matches
        assertEquals(BigDecimal.valueOf(5.98), actualCart.getTotal()); // Assert the total price is correct
    }

    // Test case for removing an item from the cart when the user is not found
    @Test
    public void removeCartNotFound() {
        // Create a ModifyCartRequest to remove an item
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(0);
        modifyCartRequest.setQuantity(1);

        // Mock the userRepository to return null (user not found)
        when(userRepository.findByUsername("test")).thenReturn(null);

        // Call the controller method to remove the item from the cart
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assert the response is not null and has a status of 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // Test case for removing an item from the cart when the item is not found
    @Test
    public void removeCartItemNotFound() {
        User user = createUserWithCart(); // Create a test user with a cart

        // Create a ModifyCartRequest to remove an item that doesn't exist
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);

        // Mock the userRepository to return the user and itemRepository to return empty (item not found)
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the controller method to remove the item from the cart
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assert the response is not null and has a status of 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
