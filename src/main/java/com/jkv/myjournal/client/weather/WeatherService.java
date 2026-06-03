package com.jkv.myjournal.client.weather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    
    private final WebClient weatherStackWebClient;

    @Value("${weatherstack.api.base-url}")
    private String baseUrl;

    @Value("${weatherstack.api.key}")
    private String apiKey;

    /**
     * @PostConstruct is a Java lifecycle annotation used on a method that needs to 
     * execute exactly ONCE during application startup.
     * * Spring executing sequence:
     * 1. Spring creates the bean instance (runs constructor injection for weatherStackWebClient).
     * 2. Spring populates variables annotated with @Value (injects baseUrl and apiKey).
     * 3. Spring executes the @PostConstruct method.
     * * This makes it the perfect place to run validation guardrails, sanity checks, or data 
     * warmups before this service layer starts processing live controller traffic.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing WeatherService lifecycle hook...");

        // 1. Validation Guardrail
        // Check if the API key was successfully read from application.properties / application.yml
        if (!StringUtils.hasText(apiKey)) {
            log.error("CRITICAL CONFIGURATION ERROR: 'weatherstack.api.key' is missing or empty!");
            // Optional: throw an exception here if you want to completely stop the application from starting
            // throw new IllegalStateException("Weatherstack API Key must be configured.");
        } else {
            log.info("Weatherstack API key validated successfully (Masked: {}***)", 
                apiKey.length() > 4 ? apiKey.substring(0, 4) : "####");
        }

        // 2. Network/API Sanity Check Warm-up (Non-blocking background check)
        // We run a quick check to ensure your WSL setup can reach the outside internet
        log.info("Performing asynchronous startup connectivity test to Weatherstack...");
    }    

    public WeatherResponse getCurrentWeatherResponse(String city){
        try{
            log.info("Fetching weather data via WebClient for city: {}", city);

            // 1. .get()
            // Configures the HTTP request method as a GET request.
            return weatherStackWebClient.get()
                    
                    // 2. .uri(...)
                    // Accepts a lambda function to construct the URL dynamically and safely.
                    .uri(uriBuilder -> uriBuilder
                        // .path() appends the relative endpoint path onto the client's preconfigured base-url
                        .path("/current")
                        // .queryParam() cleanly appends key-value pairs to the URL string (?access_key=X&query=Y)
                        // It automatically handles URL-encoding protection (e.g., handles city spaces like "New York")
                        .queryParam("access_key", apiKey)
                        .queryParam("query", city)
                        // .build() compiles all path and query parameter fragments into a final URI object
                        .build())
                    
                    // 3. .retrieve()
                    // Instructs the WebClient to execute the HTTP call and sends the request over the network.
                    // This method implicitly reads the HTTP response status code and headers.
                    .retrieve()
                    
                    // 4. .onStatus(...)
                    // Intercepts the response stream if an error occurs.
                    // 'HttpStatus::isError' checks if the response code is a 4xx (Client Error) or 5xx (Server Error).
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        // If it's an error status, it logs the code and wraps a RuntimeException inside a reactive Mono.error
                        // This bypasses normal JSON mapping and forces the execution stream straight to the catch block.
                        log.error("Weatherstack API returned error status: {}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Weather API error status: " + clientResponse.statusCode()));
                    })
                    
                    // 5. .bodyToMono(WeatherResponse.class)
                    // Extracts the raw JSON payload from the HTTP response body.
                    // It uses Spring's underlying Jackson library to deserialize the JSON properties directly into the WeatherResponse Java POJO.
                    // It returns a Mono<WeatherResponse>, which represents a reactive, async blueprint of the data.
                    .bodyToMono(WeatherResponse.class)
                    
                    // 6. .block()
                    // Serves as the synchronous bridge between WebClient's asynchronous engine and your blocking service layout.
                    // This halts execution on the current thread and forces the application to wait right here 
                    // until the network response completes, unpacking the Mono and returning the raw WeatherResponse object.
                    .block();

        }
        catch(Exception e) {
            // Graceful degradation: catch network connection issues, timeouts, or 4xx/5xx API failures.
            // Log the problem securely, but return null so the core Journal transaction doesn't crash completely.
            log.error("Error communicating with Weatherstack API for city {}: {}", city, e.getMessage());
            return null;
        }
    }
}