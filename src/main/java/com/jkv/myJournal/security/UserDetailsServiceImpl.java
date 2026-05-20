package com.jkv.myJournal.security;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.UserRepository;


/**
 * This class is the heart of your custom authentication setup.
 * It acts as the direct link between your database and Spring Security during a login attempt.
 */

@Service // Tells Spring to manage this class
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired // Connects your database repository
    private UserRepository userRepository;

    // This method handles looking up users when they try to log in
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Look up the user in your database by their username
        UserEntity user = userRepository.getByUserName(username);
        
        // 2. If the user doesn't exist in the database, throw an error
        if (user == null) {
            throw new UsernameNotFoundException("username not found: " + username);
        }
        
        /**
         * // 3. Convert your 'UserEntity' into Spring Security's native 'UserDetails' format.
        // We use Spring's built-in User.builder() utility to map your database values across:
        return User.builder()
                .username(user.getUserName())     // Maps your DB username
                .password(user.getUserPassword()) // Maps your DB hashed password (checked against user input later)
                
                // Maps your user's roles (e.g., "USER", "ADMIN").
                // .roles() expects a String array (String[]), so we convert your collection/List of roles 
                // using .toArray(new String[0]). Spring will automatically prefix these with "ROLE_" internally.
                .roles(user.getRoles().toArray(new String[0])) 
                
                .build(); // Constructs and returns the final UserDetails object.


                OR 

                we can implement UserDetails interface and return it as shown below.
         */
        return new UserPrincipal(user);
    }
}