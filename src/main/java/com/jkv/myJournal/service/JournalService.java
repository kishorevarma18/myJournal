package com.jkv.myJournal.service;

import java.util.List;
import java.util.Optional;
import com.jkv.myJournal.entity.JournalEntityWithDb;

/**
 * Interface defining the business capabilities of the Journal application.
 * Using an interface allows for easier testing and swapping implementations later.
 * 
 * Contains the core business logic, data processing, and coordination between the controller and the repository.
 */
public interface JournalService {
    void saveEntry(JournalEntityWithDb journalEntry, String userName);
    List<JournalEntityWithDb> getAll(String userName);
    Optional<JournalEntityWithDb> getById(String id, String userName);
    boolean deleteById(String id, String userName);
    JournalEntityWithDb putById(String id, JournalEntityWithDb journalEntry, String userName);
}