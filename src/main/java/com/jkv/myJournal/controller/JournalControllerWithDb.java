package com.jkv.myJournal.controller;

import org.springframework.web.bind.annotation.*;
import com.jkv.myJournal.entity.JournalEntriesWithDb;
import com.jkv.myJournal.service.JournalService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

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

    // POST http://localhost:8080/JournalWithDb
    @PostMapping
    public boolean postEntry(@RequestBody JournalEntriesWithDb journalEntryWithDb) {
        // @RequestBody converts the JSON input from the user into a Java Object
        journalService.saveEntry(journalEntryWithDb);
        return true;
    }
    //the inward path -- Controller → Service → Repository → DB

    // GET http://localhost:8080/JournalWithDb
    @GetMapping
    public List<JournalEntriesWithDb> getAllEntries() {
        return journalService.getAll();
    }
    // the outward path -- DB → Repository → Service → Controller
    // GET http://localhost:8080/JournalWithDb/id/12345
    @GetMapping("/id/{myId}")
    public Optional<JournalEntriesWithDb> getEntryById(@PathVariable String myId) {
        // @PathVariable extracts the 'myId' from the URL path
        return journalService.getById(myId);
    }
    
    // DELETE http://localhost:8080/JournalWithDb/id/12345
    @DeleteMapping("/id/{myId}")
    public boolean deleteEntryById(@PathVariable String myId){
        return journalService.deleteById(myId);
    }
    
    // PUT http://localhost:8080/JournalWithDb/search?id=12345
    @PutMapping("/search")
    public JournalEntriesWithDb putById(@RequestParam String id, @RequestBody JournalEntriesWithDb entity) {
        // @RequestParam looks for '?id=' in the URL query string
        return journalService.putById(id, entity);
    }
}

//