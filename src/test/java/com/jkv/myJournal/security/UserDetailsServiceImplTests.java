package com.jkv.myJournal.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.UserRepository;

public class UserDetailsServiceImplTests {

    /**
     * @InjectMocks tells Mockito to create an actual instance of this class 
     * and automatically try to inject all fields annotated with @Mock (like userRepository) 
     * into it, either via constructor injection, setter injection, or field injection.
     */
    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * @Mock instructs Mockito to create a completely fake, hollow implementation 
     * of the UserRepository interface. No real database code will be executed when 
     * its methods are called; instead, it tracks interactions and allows you to stub behaviors.
     *  
     * @MockBean is a Spring Boot annotation used in integration tests (@SpringBootTest). 
     * It adds a mock directly into the Spring ApplicationContext, replacing any real bean. 
     * Since this is a pure Mockito unit test (no Spring context is loaded), standard 
     * Mockito @Mock is used because it is much faster and doesn't require booting up Spring.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * AutoCloseable is a standard Java interface representing a resource that 
     * must be closed when it is no longer needed. Mockito's openMocks method returns 
     * an AutoCloseable object representing the current mock session.
     */
    private AutoCloseable closeable;

    /**
     * @BeforeEach flags this method to execute automatically BEFORE every single 
     * individual @Test method in this class runs. It is used here to initialize a clean, 
     * fresh mock setup for each test environment so tests don't cross-contaminate.
     */
    @BeforeEach
    public void setup(){
        /**
         * MockitoAnnotations.openMocks(this) scans this specific class ('this') 
         * for @Mock and @InjectMocks annotations, instantiates them, and binds them. 
         * It opens an isolated Mockito session and returns an AutoCloseable handle to close it later.
         */
        closeable = MockitoAnnotations.openMocks(this);
    }

    /**
     * @AfterEach flags this method to execute automatically AFTER every single 
     * individual @Test method finishes (whether it passes or throws an error). It is 
     * used for cleanup tasks.
     */
    @AfterEach
    public void close() throws Exception {
        /**
         * Closing the AutoCloseable session explicitly tells Mockito to release 
         * any resources or memory associated with the mocks, preventing memory leaks 
         * and ensuring no stubbed states bleed into subsequent tests.
         */
        closeable.close();
    }

    @Test
    public void loadUserByUsername(){
        /**
         * when(...).thenReturn(...) is the core syntax for "stubbing" behavior. 
         * It tells the mock: "When this specific method is called, intercept it and 
         * immediately return this specific value instead of executing real logic."
         * * ArgumentMatchers.anyString() is a flexible wildcard matcher. Instead of 
         * hardcoding a specific username string like "ram", it tells Mockito to trigger 
         * this return value for absolutely ANY string argument passed to getByUserName().
         */
        when(userRepository.getByUserName(ArgumentMatchers.anyString()))
            .thenReturn(new UserEntity("ram", "ram"));
            
        // Act: Invoke the target logic we are trying to isolate and test
        UserDetails user = userDetailsServiceImpl.loadUserByUsername("ram");
        
        // Assert: Ensure the service processed the mocked database response correctly
        assertNotNull(user);
    }
}