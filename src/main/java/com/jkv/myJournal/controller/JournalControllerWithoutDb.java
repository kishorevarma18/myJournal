package com.jkv.myJournal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.jkv.myJournal.entity.JournalEntriesWithoutDb;

/**
 * @RestController: Marks this class as a Web Controller. 
 * It combines @Controller and @ResponseBody, meaning the data returned 
 * from these methods is written directly into the HTTP response body (usually as JSON).
 */
@RestController
/**
 * @RequestMapping("/Journal"): Sets the base URL path for all APIs in this class.
 * Any request starting with /Journal will be routed here.
 */
@RequestMapping("/JournalWithoutDb")
public class JournalControllerWithoutDb {
    /**
     * WHY HASHMAP FOR STORAGE?
     * 1. Fast Access: We use the ID as a 'Key' to find entries instantly (O(1) time). 
     *    In an ArrayList, we'd have to loop through every item to find an ID (O(n) time).
     * 2. Key Uniqueness: Maps naturally prevent duplicate IDs.
     * 3. Easy Updates: map.put(id, entry) automatically replaces old data if the ID exists.
     */
    private Map<Long, JournalEntriesWithoutDb> journalEntriesMap = new HashMap<>();

    /**
     * @PostMapping: Maps HTTP POST requests to this method. 
     * Typically used to CREATE new resources.
     * 
     * @RequestBody: Tells Spring to deserialize the incoming JSON from the 
     * request body into the 'JournalEntries' object.
     */
    @PostMapping
    public Boolean postEntry(@RequestBody JournalEntriesWithoutDb entry) {
        journalEntriesMap.put(entry.getId(), entry);
        return true;
    }

    /**
     * @GetMapping: Maps HTTP GET requests to this method.
     * Used to READ or retrieve data.
     */
    @GetMapping
    public List<JournalEntriesWithoutDb> getAll() {
        return new ArrayList<>(journalEntriesMap.values());
    }
    /**
     * WHY ARRAYLIST FOR RETURNING?
     * 1. JSON Standard: APIs usually return collections as JSON Arrays [{}, {}]. 
     *    An ArrayList converts perfectly to this format.
     * 2. Clean Response: Returning the Map would include the Keys in the JSON output, 
     *    which the frontend usually doesn't need if the ID is already inside the object.
     * 3. Ordering: Lists are easier for the frontend to iterate through for display.
     */


    /**
     * @DeleteMapping: Maps HTTP DELETE requests to this method.
     * Used to REMOVE a resource.
     * 
     * @PathVariable: Extracts the value from the URI (e.g., /Journal/id/5) 
     * and binds it to the 'myId' parameter.
     */
    @DeleteMapping("/id/{myId}")
    public Boolean deleteEntry(@PathVariable long myId) {
        journalEntriesMap.remove(myId);
        return true;
    }

    /**
     * @RequestParam: Extracts data from the URL query string (e.g., /Journal/search?id=5).
     * Unlike PathVariable, this is used for filtering or optional parameters.
     */
    @GetMapping("/search")
    public List<JournalEntriesWithoutDb> getEntryByParam(@RequestParam Long id) {
        List<JournalEntriesWithoutDb> list = new ArrayList<>();
        JournalEntriesWithoutDb entry = journalEntriesMap.get(id);
        if (entry != null) {
            list.add(entry);
        }
        return list;
    }

    /**
     * @PutMapping: Maps HTTP PUT requests to this method.
     * Used to UPDATE an existing resource or replace it.
     */
    @PutMapping("/id/{myId}")
    public boolean putEntry(@RequestBody JournalEntriesWithoutDb entry, @PathVariable long myId) {
        journalEntriesMap.put(myId, entry);
        return true;
    }
}
