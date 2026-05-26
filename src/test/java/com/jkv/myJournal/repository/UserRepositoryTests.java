package com.jkv.myJournal.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.jkv.myJournal.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @DataMongoTest focuses ONLY on MongoDB components.
 * It disables full auto-configuration and instead applies only configuration
 * relevant to MongoDB tests (like configuring an embedded MongoDB if available,
 * configuring MongoTemplate, and scanning for Spring Data Mongo repositories).
 */
@DataMongoTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    /**
     * @Test signals to the JUnit Jupiter testing framework that this 
     * specific method is a test case that should be executed.
     */
    @Test
    /**
     * @Test marks this as a executable test case.
     * * @Transactional isolates this test case by using automated database transactions:
     * 1. BEFORE the test runs: Spring starts a new database transaction.
     * 2. DURING the test runs: Any database insertions, updates, or deletes are visible 
     * only within this specific test method.
     * 3. AFTER the test finishes: Spring automatically ROLLS BACK the entire transaction, 
     * meaning no changes are permanently saved. This keeps the database clean and 
     * prevents tests from leaking "dirty data" into subsequent tests.
     * * NOTE FOR MONGO DB: MongoDB transactions require a Replica Set setup. If you are 
     * running a basic standalone MongoDB instance locally, this annotation will be ignored 
     * or may throw an exception, requiring manual cleanup in an @AfterEach method instead.
     */
    @Transactional
    public void InvalidUserTest(){
        
        /**
         * assertThatThrownBy is an AssertJ assertion tool used to verify that 
         * a specific block of code (provided as a lambda expression) throws an exception.
         * * Here, it runs the code inside the lambda and verifies that the resulting 
         * exception is an instance of 'DuplicateKeyException.class'. If the code 
         * succeeds without throwing this exact exception, the test will fail.
         */
        assertThatThrownBy(() -> {
            UserEntity invalidUser = new UserEntity("vinesh", "Vinesh@123");
            userRepository.save(invalidUser);
        }).isInstanceOf(DuplicateKeyException.class);
    }
}