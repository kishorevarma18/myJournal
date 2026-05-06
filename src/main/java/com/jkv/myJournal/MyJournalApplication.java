package com.jkv.myJournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}
