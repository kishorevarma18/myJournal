package com.jkv.myJournal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Tells Spring that this class contains bean definitions for the application context.
@EnableWebSecurity // Enables Spring Security's web security support and integrates it with Spring MVC.
public class SecurityConfig {
    
    // Injecting your custom implementation that loads user-specific data from your database.
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;


    /**
     * The SecurityFilterChain bean defines which database/HTTP requests are secure, 
     * which ones require authentication, and how users are authenticated.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Disables CSRF (Cross-Site Request Forgery) protection. Usually done for REST APIs 
            // since they are stateless and don't rely on browser cookies for session management.
            .csrf(customizer -> customizer.disable())
            
            // Defines authorization rules for specific HTTP requests.
            .authorizeRequests(requests -> requests
                // Allows anyone to access endpoints starting with "/public/" without logging in.
                .antMatchers("/public/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                /*
                // below code also works, but .hasRole("ADMIN") is the standard practice
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // in order to use .hasRole("ADMIN")
                // we have provide new SimpleGrantedAuthority("ROLE_" + role) in getAuthorities method in UserPrincipal class
                
                // if you don't want to include ROLE_ in SimpleGrantedAuthority then use .hasAuthority("ADMIN") in securityFilterChain instead of .hasRole("ADMIN").
                // cause .hasRole("ADMIN") will look for "ROLE_ADMIN".
                */

                // Every other request not covered above MUST be authenticated.
                .anyRequest().authenticated())
            
            // Form login is commented out because this configuration is set up for a stateless API.
            // //.formLogin(form -> form.permitAll())
            
            // Configures the application to be completely stateless. Spring Security won't 
            // create or use an HTTP session to store the user's security context.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Enables Basic HTTP Authentication (Username/Password sent in the headers).
            .httpBasic(Customizer.withDefaults())
            
            // Builds and returns the configured HttpSecurity object.
            .build();
    }

    /**
     * An AuthenticationProvider is responsible for processing a specific type of authentication request.
     * DaoAuthenticationProvider is a standard implementation that leverages a UserDetailsService 
     * and a PasswordEncoder to validate a username and password.
     */

    /**
     * 
     * the AuthenticationManager bean. 
     * Spring Security automatically detects your UserDetailsService and PasswordEncoder 
     * beans and sets up a DaoAuthenticationProvider behind the scenes inside this manager.
     *
     *  @Bean
     *  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
     *      return config.getAuthenticationManager();
     *  }
     * 
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Tells the provider to use BCrypt to hash and verify passwords.
        provider.setPasswordEncoder(passwordEncoder());
        // Tells the provider how to fetch the user from the database.
        provider.setUserDetailsService(userDetailsServiceImpl);
        return provider;
    }
    


    /**
     * Exposes a PasswordEncoder bean. BCrypt is a secure, industry-standard 
     * password hashing algorithm that automatically handles salting.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}