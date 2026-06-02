package com.jkv.myjournal.service;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.jkv.myjournal.entity.UserEntity;

/**
 * ArgumentsProvider is a JUnit Jupiter interface used to supply a custom stream of 
 * arguments to a @ParameterizedTest method. 
 * * Implementing this interface is incredibly useful when your test cases require 
 * complex objects (like instantiated Entities, mock data, or database records) 
 * that cannot be easily written inside simple annotations like @ValueSource or @CsvSource.
 * * To use this provider in a test class, you would annotate your test method with:
 * @ArgumentsSource(UserArgumentProvider.class)
 */
public class UserArgumentProvider implements ArgumentsProvider {

    /**
     * The provideArguments method is the single abstract method required by the 
     * ArgumentsProvider interface. JUnit automatically calls this method behind 
     * the scenes to gather the test data matrix.
     * * @param context Provides access to the current test execution context (e.g., test class or method details).
     * @return A Stream of Arguments objects, where each Arguments instance represents a single test run.
     * @throws Exception if any error occurs during the preparation of test data.
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        
        /**
         * Stream.of(...) creates a sequential stream containing the data sets.
         * * Arguments.of(...) wraps the comma-separated values into a single test case row.
         * The objects inside Arguments.of() are mapped sequentially to the parameters 
         * of your test method. 
         * * For example, the corresponding test method signature should expect two arguments:
         * public void myTest(String description, UserEntity user)
         */
        return Stream.of(
            Arguments.of("Kishore", new UserEntity("Tejaswi", "Tejaswi@123")),
            Arguments.of("Varma", new UserEntity("Ashita", "Ashita@123")),
            Arguments.of("Jampani", new UserEntity("Vinesh", "Vinesh@123"))
        );
    }
}