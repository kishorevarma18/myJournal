package com.jkv.myJournal.controller;

import org.springframework.web.bind.annotation.*;
import com.jkv.myJournal.entity.JournalEntityWithDb;
import com.jkv.myJournal.service.JournalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    // POST http://localhost:8080/JournalWithDb
    @PostMapping("/{userName}")
    public ResponseEntity<?> postEntry(@PathVariable String userName,@RequestBody JournalEntityWithDb journalEntryWithDb) {
        // @RequestBody converts the JSON input from the user into a Java Object
        if(journalEntryWithDb == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body can't be empty!");
        }
        try{
            journalService.saveEntry(journalEntryWithDb,userName);
            return ResponseEntity.status(200).body(journalEntryWithDb);
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured");
        }
            
        
        
    }
    //the inward path -- Controller → Service → Repository → DB

    /**
     * EXPLANATION:
     * .build(): Used when you want to send a Status Code but NO body content. 
     * It "builds" the ResponseEntity without a payload.
     */
    // GET http://localhost:8080/JournalWithDb
    @GetMapping("/{userName}")
    public ResponseEntity<?> getAllEntries(@PathVariable String userName) {
        try{
            if(journalService.getAll(userName).isEmpty()){
                // .build() is used here because NO_CONTENT (204) technically shouldn't have a body.
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(journalService.getAll(userName));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured");
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
    // GET http://localhost:8080/JournalWithDb/id/12345
    @GetMapping({"/id/{myId}","/id/"})
    public ResponseEntity<?> getEntryById(@PathVariable(required=false) String myId) {
        // @PathVariable extracts the 'myId' from the URL path
        if(StringUtils.hasText(myId)){
            return ResponseEntity.status(HttpStatus.OK).body(journalService.getById(myId));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
    }
    
    // DELETE http://localhost:8080/JournalWithDb/id/12345
    @DeleteMapping({"/{userName}/id/{myId}","/{userName}/id/"})
    public ResponseEntity<?> deleteEntryById(@PathVariable(required=false) String myId, @PathVariable String userName){
        // Better practice: Use StringUtils.hasText(myId) here too for consistency!
        try{
            if(myId != null && !myId.equals("") && !myId.equals(" ")){
                return ResponseEntity.status(HttpStatus.OK).body(journalService.deleteById(myId,userName));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured");
        }
    }
    
    // PUT http://localhost:8080/JournalWithDb/search?id=12345
    @PutMapping("/search/{userName}")
    public ResponseEntity<?> putById(@RequestParam String id, @RequestBody JournalEntityWithDb entity, @PathVariable String userName) {
        // @RequestParam looks for '?id=' in the URL query string
        try{
            if(entity == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("body can't be null or empty");
            }
            else if(id != null && !id.equals("") && !id.equals(" ")){
                if(journalService.putById(id, entity, userName)==null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not matched!");
                }
                return ResponseEntity.status(HttpStatus.OK).body(journalService.putById(id, entity,userName));
            }
            else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id can't be null or empty");
            }
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occured");
        }
    }
}