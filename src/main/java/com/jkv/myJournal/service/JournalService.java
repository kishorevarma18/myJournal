package com.jkv.myJournal.service;

import java.util.List;
import java.util.Optional;
import com.jkv.myJournal.entity.JournalEntriesWithDb;

/**
 * Interface defining the business capabilities of the Journal application.
 * Using an interface allows for easier testing and swapping implementations later.
 * 
 * Contains the core business logic, data processing, and coordination between the controller and the repository.
 */
public interface JournalService {
    void saveEntry(JournalEntriesWithDb journalEntry);
    List<JournalEntriesWithDb> getAll();
    Optional<JournalEntriesWithDb> getById(String id);
    boolean deleteById(String id);
    JournalEntriesWithDb putById(String id, JournalEntriesWithDb journalEntry);
}