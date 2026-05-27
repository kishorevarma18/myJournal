package com.jkv.myJournal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.UserRepository;

/**
 * @SpringBootTest tells Spring Boot to look for a main configuration class 
 * (like one annotated with @SpringBootApplication) and use that to start a 
 * FULL application context. Unlike @DataMongoTest (which only loads database slices), 
 * this annotation loads everything—your services, repositories, security beans, 
 * controllers, etc. This is perfect for integration testing.
 */
@SpringBootTest
public class UserServiceImplTests {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    /**
     * @ParameterizedTest indicates that this method is a test case, but unlike 
     * a standard @Test, it will be executed MULTIPLE times using different arguments.
     * * @ValueSource is a simple argument source. It provides an array of literal values 
     * (in this case, Strings) to a single parameter in your test method. JUnit will 
     * run this test 5 individual times, passing one name from the list on each run.
     */
    @ParameterizedTest
    @ValueSource(strings={
        "Admin",
        "Varma"
    })
    public void testGetByUserName(String name){
        /**
         * assertNotNull is a traditional JUnit 5 assertion. 
         * It verifies that the object returned by 'userRepository.getByUserName(name)' 
         * is not null. If the object is null, the test fails instantly and prints 
         * the custom failure message: "failed for: " + name.
         */
        assertNotNull(userRepository.getByUserName(name),"failed for: "+name);
    }

    /**
     * @ParameterizedTest is used here again because this method accepts multiple inputs.
     * * @CsvSource allows you to express argument lists as comma-separated values (CSV). 
     * This is used when your test method needs multiple arguments per run. Each string 
     * in the array represents a single test run, and the comma splits the values into 
     * the method's parameters (e.g., "user," sets name="user" and password=null).
     */
    @Transactional
    @ParameterizedTest
    @CsvSource({
        "user,",
        ",nouser@123"
    })
    public void TestSaveUser(String name, String password){
        String passwordEncode = (password != null) ? passwordEncoder.encode(password) : null;
        if(name == null || password == null){
            assertThatThrownBy(() -> new UserEntity(name, passwordEncode)).isInstanceOf(NullPointerException.class);
            return;
        }
        UserEntity newUser = new UserEntity(name, passwordEncode);
        userService.saveUser(newUser);

        /**
         * assertThat comes from AssertJ and provides a fluent, human-readable assertion style.
         * Instead of traditional parameter orders, it reads like a sentence: "Assert that 
         * this username IS EQUAL TO the expected name". It allows you to chain multiple 
         * checks together on the same object if needed.
         */
        assertThat((userRepository.getByUserName(name)).getUserName()).isEqualTo(name);

        /**
         * assertNotNull (JUnit 5) is used here as a final sanity check to guarantee 
         * that the user was actually persisted and can be successfully pulled back 
         * from the database without resulting in a null reference.
         */
        assertNotNull(userRepository.getByUserName(name));
    }
}