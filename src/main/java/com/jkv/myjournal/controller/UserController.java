package com.jkv.myjournal.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jkv.myjournal.client.weather.WeatherResponse;
import com.jkv.myjournal.client.weather.WeatherService;
import com.jkv.myjournal.entity.UserEntity;
import com.jkv.myjournal.security.UserPrincipal;
import com.jkv.myjournal.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    // @GetMapping
    // public ResponseEntity<?> getUsers(){
    //     if(!userService.getAll().isEmpty())
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).body("no data");    
    // }   
    
    // @GetMapping({"/id/{myId}","/id"})
    // public ResponseEntity<?> getUserById(@PathVariable(required = false) String myId){
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.getById(myId));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    // @PutMapping({"id/{myId}","/id"})
    // public ResponseEntity<?> updateUserById(@PathVariable(required = false) String myId, @RequestBody UserEntity newUser) {
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(myId,newUser));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    // @DeleteMapping({"id/{myId}","/id"})
    // public ResponseEntity<?> deleteUserById(@PathVariable(required = false) String myId){
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.deleteById(myId));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    /*
     * CONCLUSION & SUMMARY COMPARISON:
     * 
     * • SecurityContextHolder: The manual approach. Best used when you are deep in a service layer 
     *   or a background thread where Spring cannot automatically inject method arguments.
     * • Principal: The standard Java EE approach. Good if you only ever need a simple String username, 
     *   but limited if you want to access custom user properties.
     * • @AuthenticationPrincipal: The modern Spring **BEST PRACTICE**. Best to use in Controllers because 
     *   it gives you immediate, strongly-typed access to your custom UserPrincipal object (and all its 
     *   extra fields like ID or email) without requiring extra database lookups.
     */

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody(required = false) UserEntity newUser) {
        /*
         * SecurityContextHolder is the central vault where Spring Security stores details 
         * about who is currently authenticated. By calling .getContext().getAuthentication(), 
         * you are manually pulling the current authentication token out of that vault to 
         * inspect its properties (like the username).
         */
        Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
        String userName = authenticatedUser.getName();
        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateByName(userName, newUser));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body should not be null or empty");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Principal principal) {
        /*
         * Principal is a standard Java interface (java.security.Principal) representing the 
         * identity of the logged-in user. By injecting it as a method argument, Spring MVC 
         * automatically populates it for you. It's a quick, clean way to grab the user's 
         * unique identity name without digging into Spring-specific classes.
         */
        String userName = principal.getName();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.deleteByName(userName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); 
        }
    }

    @GetMapping
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        /*
         * @AuthenticationPrincipal is a specialized annotation that tells Spring Security to 
         * inject your custom user detail object directly into the method. 
         * 
         * UserPrincipal is your custom class (usually implementing UserDetails). Instead of 
         * just getting a generic string name (like with Principal), this gives you direct, 
         * strongly-typed access to your domain-specific user data (e.g., custom IDs, emails, 
         * or roles) tied to that logged-in session.
         */
        String userName = userPrincipal.getUsername();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getByUserName(userName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getWeatherforUser(@AuthenticationPrincipal UserPrincipal userPrincipal){
        String userName = userPrincipal.getUsername();
        String cityName = userPrincipal.getCity();
        WeatherResponse weatherResponse = weatherService.getCurrentWeatherResponse(cityName);
        try{
            /*
            String	%s ,int	%d, double/float	%f, double with 1 decimal	%.1f, double with 2 decimals	%.2f, New line	%n
            boolean %b, char %c.
            Format Specifiers Cheat Sheet
            */
            return ResponseEntity.status(HttpStatus.OK).body(
                String.format("Hi %s!!%nCurrent Location: %s%nCurrent Temparature: %d C%nFeels like: %d C",
                    userName,
                    cityName,
                    weatherResponse.getCurrent().getTemperature(),
                    weatherResponse.getCurrent().getFeelslike()
                )
            );
            /*
            //this provides the json response which most applications accepts.
            Map<String, Object> response = Map.of(
                "city",cityName,
                "temperature", weatherResponse.getCurrent().getTemperature(),
                "feelsLike", weatherResponse.getCurrent().getFeelslike()
            );
            return ResponseEntity.ok(response);
            */
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
