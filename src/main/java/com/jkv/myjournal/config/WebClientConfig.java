package com.jkv.myjournal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.NonNull;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient weatherstackWebClient(@Value("${weatherstack.api.base-url}") @NonNull String baseUrl) {
        // WebClient.builder() provides a mutable builder fluent API to customize the HTTP client engine.
        return WebClient.builder()
                // .baseUrl() establishes a root domain for all outbound HTTP requests made by this bean instance.
                // This allows individual services (like WeatherService) to provide only the relative endpoint 
                // path (like "/current") instead of concatenating full raw URL strings every time.
                .baseUrl(baseUrl)
                // .build() compiles the configuration parameters and produces the final immutable WebClient instance.
                .build();
    }
}
