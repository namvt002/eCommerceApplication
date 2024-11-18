package com.example.demo.controllers;

// Import necessary classes for testing, mocking, and assertions
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
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

public class OrderControllerTest {

    // Declare variables for the OrderController and repositories
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);  // Mock UserRepository
    private final OrderRepository orderRepository = mock(OrderRepository.class);  // Mock OrderRepository
    
    @Before
    public void setUpMocked() {
        orderController = new OrderController();  // Initialize the OrderController
        injectDependenciesMocked(orderController, "userRepository", userRepository);  // Inject the mock UserRepository
        injectDependenciesMocked(orderController, "orderRepository", orderRepository);  // Inject the mock OrderRepository
    }

    private static void injectDependenciesMocked(Object target, String fieldName, Object dependency) {
        try {
            // Use reflection to access the private field in the target object
            Field field = target.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);  // Set the field accessible
            field.set(target, dependency);  // Inject the dependency
            field.setAccessible(accessible);  // Restore the original accessibility of the field
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);  // If an error occurs, throw a RuntimeException
        }
    }

    @Test
    public void submitOrder() {
        // Create a User with a Cart and some items
        User user = createUserWithCart(new BigDecimal("2.99"));
        // Mock the userRepository to return the user when findByUsername is called with "test"
        when(userRepository.findByUsername("test")).thenReturn(user);

        // Call the submit method of the OrderController with username "test"
        final ResponseEntity<UserOrder> response = orderController.submit("test");

        // Assert that the response is not null and the status code is 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the UserOrder from the response body
        UserOrder actualUserOrder = response.getBody();
        assertNotNull(actualUserOrder);  // Ensure the UserOrder is not null

        // Assert that the items, user, and total match the expected values
        assertEquals(user.getCart().getItems(), actualUserOrder.getItems());
        assertEquals(user, actualUserOrder.getUser());
        assertEquals(user.getCart().getTotal(), actualUserOrder.getTotal());
    }

    @Test
    public void submitOrderNotFound() {
        // Mock the userRepository to return null when findByUsername is called with "test"
        when(userRepository.findByUsername("test")).thenReturn(null);

        // Call the submit method with username "test"
        final ResponseEntity<UserOrder> response = orderController.submit("test");

        // Assert that the response is not null and the status code is 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() {
        // Create a User with a Cart and some items
        User user = createUserWithCart(new BigDecimal("2.99"));
        // Create a list of orders for the user
        List<UserOrder> userOrders = createUserOrders(user);
        // Mock the userRepository and orderRepository to return the user and orders
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        // Call the getOrdersForUser method with username "test"
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        // Assert that the response is not null and the status code is 200 (OK)
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the list of UserOrders from the response body
        List<UserOrder> actualUserOrders = response.getBody();
        assertNotNull(actualUserOrders);  // Ensure the actual orders are not null

        // Assert that the expected and actual orders match
        assertEquals(userOrders, actualUserOrders);
    }


    @Test
    public void getOrdersForUserNotFound() {
        // Mock the userRepository to return null when findByUsername is called with "test"
        when(userRepository.findByUsername("test")).thenReturn(null);

        // Call the getOrdersForUser method with username "test"
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        // Assert that the response is not null and the status code is 404 (Not Found)
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private User createUserWithCart(BigDecimal itemPrice) {
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setId(0L);  // Set the user's ID to 0 (mock data)

        Cart cart = new Cart();
        cart.setId(0L);  // Set the cart ID to 0 (mock data)
        cart.setUser(user);  // Associate the cart with the user
        user.setCart(cart);  // Set the user's cart

        // Create a test Item object
        Item item = new Item();
        item.setId(0L);
        item.setName("itemname");
        item.setPrice(itemPrice);  // Set the item price
        item.setDescription("description");  // Set the item description

        // Create a list of 3 items and add them to the cart
        List<Item> itemsArray = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            itemsArray.add(item);  // Add the item to the list 3 times
        }
        cart.setItems(itemsArray);  // Set the cart's items
        cart.setTotal(itemPrice.multiply(BigDecimal.valueOf(3)));  // Set the total price of the cart (itemPrice * 3)

        return user;  // Return the created user with the cart and items
    }

    private List<UserOrder> createUserOrders(User user) {
        List<UserOrder> userOrders = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UserOrder order = UserOrder.createFromCart(user.getCart());  // Create a UserOrder from the user's cart
            order.setId((long) i);  // Set the order ID (mock data)
            userOrders.add(order);  // Add the order to the list
        }
        return userOrders;  // Return the list of orders
    }
}
