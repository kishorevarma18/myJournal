package com.jkv.myjournal.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.jkv.myjournal.entity.UserEntity;

/**
 * @SpringBootTest loads the standard application context.
 * * @AutoConfigureDataMongo explicitly tells Spring Boot to automatically configure 
 * the MongoDB components (like MongoTemplate, MongoClient, and repositories) 
 * inside this full context integration test. It's an excellent way to bridge 
 * full service testing with actual or embedded MongoDB interactions.
 */
@SpringBootTest
@AutoConfigureDataMongo
public class UpdateAndDeleteTests {

    @Autowired
    private UserService userService;

    /**
     * @ParameterizedTest allows running this method multiple times with different inputs.
     * * @ArgumentsSource(UserArgumentProvider.class) links this test method to a custom 
     * external class (UserArgumentProvider) that implements the ArgumentsProvider interface. 
     * It allows feeding complex objects (like the String 'name' and the 'UserEntity' object) 
     * straight into the test parameters row-by-row.
     */
    @Transactional
    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    public void TestUpdatebyName(String name, UserEntity user){
        /**
         * assertTrue is a core JUnit 5 assertion. 
         * It evaluates the boolean expression inside it—in this case, the result of 
         * userService.updateByName(name, user). If the service method returns 'true', 
         * the assertion passes. If it returns 'false', the test fails immediately.
         */
        assertTrue(userService.updateByName(name, user));
    }
    @Transactional
    @ParameterizedTest
    @CsvSource({
        "Kishore",
        "Varma",
        "Jampani"
    })
    public void TestDeletebyName(String name){
        /**
         * assertTrue checks that the deletion logic executed successfully and returned 
         * 'true'. If the user deletion failed or returned 'false', the test fails.
         */
        assertTrue(userService.deleteByName(name));
    }
}