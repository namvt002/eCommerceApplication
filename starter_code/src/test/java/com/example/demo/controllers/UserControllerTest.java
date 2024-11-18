package com.example.demo.controllers;

// Importing necessary classes for unit testing and mock objects.
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    // Declare the UserController to be tested, and mock dependencies for UserRepository, CartRepository, and BCryptPasswordEncoder.
    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);  // Mocking UserRepository
    private final CartRepository cartRepository = mock(CartRepository.class);  // Mocking CartRepository
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);  // Mocking BCryptPasswordEncoder
    
    @Before
    public void setUpMocked() {
        // Initialize the UserController object to be tested.
        userController = new UserController();
        // Inject the mocked dependencies into the UserController instance using reflection.
        injectDependenciesMocked(userController, "userRepository", userRepository);
        injectDependenciesMocked(userController, "cartRepository", cartRepository);
        injectDependenciesMocked(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }
    
    private static void injectDependenciesMocked(Object target, String fieldName, Object dependency) {
        try {
            // Use reflection to access the private field by its name.
            Field field = target.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();  // Check if the field is accessible.
            field.setAccessible(true);  // Set the field as accessible.
            field.set(target, dependency);  // Inject the dependency.
            field.setAccessible(accessible);  // Restore the original accessibility.
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);  // If there's an error, throw a RuntimeException.
        }
    }

    @Test
    public void getUserByUsername() {
        // Create a User object and a Cart for the user.
        User user = new User();
        user.setUsername("username");
        Cart cart = new Cart();
        cart.setId(0L);  // Set cart ID to 0 (mock data).
        cart.setUser(user);  // Set the user in the cart.
        user.setCart(cart);  // Set the cart for the user.
        user.setId(0);  // Set user ID to 0 (mock data).
        user.setPassword("password");

        // Mock the userRepository to return the created user when findByUsername is called with "username".
        when(userRepository.findByUsername("username")).thenReturn(user);

        // Call the findByUserName method to retrieve the user.
        final ResponseEntity<User> response = userController.findByUserName("username");

        // Assert that the response is not null and the status code is 200 (OK).
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the User object from the response body.
        User actualUser = response.getBody();
        assertNotNull(actualUser);  // Assert that the user is not null.
        assertEquals(0, actualUser.getId());  // Assert that the ID matches.
        assertEquals("username", actualUser.getUsername());  // Assert that the username matches.
        assertEquals("password", actualUser.getPassword());  // Assert that the password matches.
        assertEquals(cart, actualUser.getCart());  // Assert that the cart matches.
    }

    @Test
    public void createUserMocked() {
        // Mock the BCryptPasswordEncoder to return a hashed password.
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashed");

        // Create a CreateUserRequest with valid input values.
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        // Call the createUser method of the controller with the request.
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        // Assert that the response is not null.
        assertNotNull(response);
        // Assert that the response status code is 200 (OK).
        assertEquals(200, response.getStatusCodeValue());

        // Get the User object from the response body.
        User user = response.getBody();
        assertNotNull(user);  // Assert that the user is not null.
        assertEquals(0, user.getId());  // Assert that the user ID is 0 (mocked).
        assertEquals("username", user.getUsername());  // Assert that the username matches.
        assertEquals("hashed", user.getPassword());  // Assert that the password is hashed.
    }

    @Test
    public void createUserInvalidPasswordMocked() throws Exception {
        // Mock the BCryptPasswordEncoder to return a hashed password.
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashed");

        // Create a CreateUserRequest with mismatched passwords.
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password1");

        // Call the createUser method and check the response.
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        // Assert that the response is not null and the status code is 400 (Bad Request).
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getUsernameById() {
        // Create a User object and a Cart for the user.
        User user = new User();
        user.setUsername("username");
        Cart cart = new Cart();
        cart.setId(0L);  // Set cart ID to 0 (mock data).
        cart.setUser(user);  // Set the user in the cart.
        user.setCart(cart);  // Set the cart for the user.
        user.setId(0);  // Set user ID to 0 (mock data).
        user.setPassword("password");

        // Mock the userRepository to return the created user when findById is called with ID 0.
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));

        // Call the findById method to retrieve the user.
        final ResponseEntity<User> response = userController.findById(0L);

        // Assert that the response is not null and the status code is 200 (OK).
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the User object from the response body.
        User actualUser = response.getBody();
        assertNotNull(actualUser);  // Assert that the user is not null.
        assertEquals(0, actualUser.getId());  // Assert that the ID matches.
        assertEquals("username", actualUser.getUsername());  // Assert that the username matches.
        assertEquals("password", actualUser.getPassword());  // Assert that the password matches.
        assertEquals(cart, actualUser.getCart());  // Assert that the cart matches.
    }
}
