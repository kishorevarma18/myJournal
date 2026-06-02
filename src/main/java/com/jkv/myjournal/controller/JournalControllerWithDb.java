package com.jkv.myjournal.controller;

import org.springframework.web.bind.annotation.*;

import com.jkv.myjournal.entity.JournalEntityWithDb;
import com.jkv.myjournal.security.UserPrincipal;
import com.jkv.myjournal.service.JournalService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;

/*
Manages the API endpoints, handles incoming HTTP requests, and returns responses to the client.
*/

/**
 * @RestController identifies this as a web controller where return values are written 
 * directly to the HTTP response body (usually as JSON).
 * @RequestMapping defines the base URL for all endpoints in this class.
 */
@RestController
@RequestMapping("/JournalWithDb")
public class JournalControllerWithDb {

    @Autowired
    private JournalService journalService; // Injecting the "Brain"

    /**
     * EXPLANATION:
     * ResponseEntity<?>: A wrapper that represents the entire HTTP response (Status Code, Headers, and Body).
     * The '?' is a wildcard, meaning the body can be anything (a Journal object, a String error message, etc.).
     * 
     * .status(int/HttpStatus): Explicitly sets the HTTP response code (e.g., 200, 400, 404).
     * .body(Object): Sets the data returned to the client (usually converted to JSON).
     */
    @PostMapping
    public ResponseEntity<?> postUserEntry(@RequestBody JournalEntityWithDb journalEntryWithDb,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // @RequestBody converts the JSON input from the user into a Java Object
        if(journalEntryWithDb == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body can't be empty!");
        }
        try{
            String userName = userPrincipal.getUsername();
            journalService.saveEntry(journalEntryWithDb,userName);
            return ResponseEntity.status(200).body(journalEntryWithDb);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured: "+e.getMessage());
        }
            
        
        
    }
    //the inward path -- Controller → Service → Repository → DB

    /**
     * EXPLANATION:
     * .build(): Used when you want to send a Status Code but NO body content. 
     * It "builds" the ResponseEntity without a payload.
     */
    @GetMapping
    public ResponseEntity<?> getAllUserEntries(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try{
            String userName = userPrincipal.getUsername();
            List<?> userEntries = journalService.getAll(userName);
            if(userEntries.isEmpty()){
                // .build() is used here because NO_CONTENT (204) technically shouldn't have a body.
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(userEntries);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured: "+e.getMessage());
        }
    }
    // the outward path -- DB → Repository → Service → Controller
    
    /**
     * EXPLANATION:
     * StringUtils.hasText(myId):
     * Why it's SUPERIOR: Unlike .equals(""), this single check validates THREE things at once:
     * 1. Is it null? (Checks `myId != null`)
     * 2. Is it empty? (Checks `myId.length() > 0`)
     * 3. Is it just whitespace? (Checks if it contains at least one non-space character).
     * It prevents errors if a user sends a space like "/id/%20".
     */
    @GetMapping({"/id/{myId}","/id/","/id"})
    public ResponseEntity<?> getUserEntryById(@PathVariable(required=false) String myId,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // @PathVariable extracts the 'myId' from the URL path
        try{
            if(StringUtils.hasText(myId)){
            String userName = userPrincipal.getUsername();
            Optional<?> fetchedEntries=journalService.getById(myId,userName);
            if(fetchedEntries.isPresent())
                return ResponseEntity.status(HttpStatus.OK).body(fetchedEntries);
            else
                return ResponseEntity.status(HttpStatus.OK).body(userName+" doesn't have the id or id is invalid");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured: "+e.getMessage());
        }
    }
    
    @DeleteMapping({"/id/{myId}","/id/","/id"})
    public ResponseEntity<?> deleteUserEntryById(@PathVariable(required=false) String myId, @AuthenticationPrincipal UserPrincipal userPrincipal){
        // Better practice: Use StringUtils.hasText(myId) here too for consistency!
        try{
            String userName = userPrincipal.getUsername();
            if(StringUtils.hasText(myId)){
                return ResponseEntity.status(HttpStatus.OK).body(journalService.deleteById(myId,userName));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured: "+e.getMessage());
        }
    }
    
    @PutMapping("/id")
    public ResponseEntity<?> putUserEntryById(@RequestParam String myId,
                                    @RequestBody(required = false) JournalEntityWithDb entity,
                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // @RequestParam looks for '?id=' in the URL query string
        try{
            String userName = userPrincipal.getUsername();
            if(entity == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("body can't be null or empty");
            }
            else if(StringUtils.hasText(myId)){
                JournalEntityWithDb result =journalService.putById(myId, entity, userName);
                if(result==null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userName+" doesn't have the id or id is invalid");
                }
                return ResponseEntity.status(HttpStatus.OK).body(journalService.putById(myId, entity,userName));
            }
            else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured");
        }
    }
}