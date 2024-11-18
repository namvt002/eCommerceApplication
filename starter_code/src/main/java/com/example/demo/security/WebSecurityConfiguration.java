package com.example.demo.security;

// Import necessary classes for security configuration and services
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Static imports for constant URLs for sign-in and sign-up
import static com.example.demo.util.Constant.SIGN_IN_URL;
import static com.example.demo.util.Constant.SIGN_UP_URL;

// Enable web security in the application using Spring Security
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    // Inject the custom UserDetailsService implementation and BCryptPasswordEncoder
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Constructor to initialize the userDetailsService and bCryptPasswordEncoder
    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // Override the authenticationManagerBean method to expose the AuthenticationManager as a Bean
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); // Calls the superclass method to return the AuthenticationManager
    }

    // Configure HTTP security settings such as which URLs are accessible and session management
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()  // Enable Cross-Origin Resource Sharing (CORS)
                .csrf().disable()  // Disable CSRF protection, typically used for stateless API (JWT)
                .authorizeRequests()  // Begin authorization settings
                .antMatchers(HttpMethod.POST, SIGN_UP_URL, SIGN_IN_URL).permitAll()  // Allow public access to the sign-up and sign-in endpoints
                .anyRequest().authenticated()  // Require authentication for any other request
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))  // Add a filter to handle authentication with JWT (for login requests)
                .addFilter(new JWTAuthenticationVerificationFilter(authenticationManager()))  // Add a filter to handle authentication verification with JWT (for each request)
                .sessionManagement()  // Configure session management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Use stateless session management (since JWTs are stateless, no server-side session storage)
    }

    // Configure the AuthenticationManagerBuilder to set up custom user details service and password encoder
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManagerBean())  // Set the parent authentication manager to the one we defined as a Bean
                .userDetailsService(userDetailsService)  // Set the custom UserDetailsService to retrieve user data
                .passwordEncoder(bCryptPasswordEncoder);  // Set the BCryptPasswordEncoder to validate passwords
    }
}
