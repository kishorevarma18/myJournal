package com.jkv.myjournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @SpringBootApplication is a "meta-annotation" that combines three key features:
 * 
 * 1. @SpringBootConfiguration: Marks the class as a source of bean definitions.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on 
 *    classpath settings, other beans, and various property settings.
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, 
 *    and services in the 'com.jkv.myJournal' package, allowing it to find your classes.
 */
@SpringBootApplication
/*
 * @EnableTransactionManagement
 * 
 * WHAT IT DOES:
 * This annotation acts as the "Master Switch" for transactions in your application.
 * 
 * HOW IT WORKS:
 * 1. SCANNING: When the application starts, this annotation tells Spring to scan 
 *    all beans (like your @Service classes) for the @Transactional annotation.
 * 
 * 2. PROXY CREATION: For every class that has @Transactional methods, Spring 
 *    automatically creates a 'Proxy' (a wrapper) around that class.
 * 
 * 3. ORCHESTRATION: The proxy is what actually interacts with the 
 *    PlatformTransactionManager you defined. It intercepts your method calls 
 *    to start a transaction before your code runs and commit/rollback after 
 *    your code finishes.
 * 
 * Without this annotation, Spring will ignore your @Transactional tags, 
 * and your database operations will not be atomic (no rollbacks).
 */
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class MyJournalApplication {

    public static void main(String[] args) {
        /**
         * SpringApplication.run() performs the following internal magic:
         * 
         * -- IOC CONTAINER (Inversion of Control) --
         * It initializes the ApplicationContext, which is the "Container." 
         * Instead of you manually creating objects with 'new', the Container 
         * takes control of the instantiation, configuration, and assembly of objects (Beans).
         * 
         * -- DEPENDENCY INJECTION (DI) --
         * Once the Container identifies your classes (via Component Scanning), it 
         * "injects" required dependencies into them at runtime. 
         * If Class A needs Class B, the Container provides the instance of B to A, 
         * ensuring loose coupling and easier testing.
         */
        SpringApplication.run(MyJournalApplication.class, args);
    }
    
    /**
    * This method defines a 'Transaction Manager' bean, which is the heart of 
    * transactional support in Spring Data MongoDB.
    * 
    * @param dbFactory The factory that Spring Boot uses to connect to your MongoDB instance.
    * @return A PlatformTransactionManager specifically designed for MongoDB.
    */
    @Bean
    public PlatformTransactionManager transactionManager(@NonNull MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    /*
    * HOW IT WORKS:
    * 1. Spring looks for a bean of type 'PlatformTransactionManager' whenever it 
    *    encounters the @Transactional annotation on a method.
    * 
    * 2. The MongoTransactionManager hooks into the MongoDB 'Client Session' API.
    * 
    * 3. When a @Transactional method starts, this manager tells MongoDB to begin 
    *    a new session and start a transaction.
    * 
    * 4. It tracks all database operations (saves, updates, deletes) performed 
    *    within that method.
    * 
    * 5. If the method finishes successfully, this manager sends a 'commit' 
    *    command to MongoDB. If an exception occurs, it sends an 'abort' command, 
    *    wiping out all changes made during the session.
    */
    }
}
