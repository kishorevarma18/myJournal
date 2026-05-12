package com.jkv.myJournal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jkv.myJournal.entity.JournalEntriesWithDb;
import com.jkv.myJournal.repository.JournalRepository;
import com.jkv.myJournal.service.JournalService;

/*
Holds the actual code implementation for the methods defined in the Service interface.
*/


/**
 * @Service tells Spring this class contains the Business Logic.
 */
@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalRepository journalRepository; // Injecting the Database "Librarian"

    @Override
    public void saveEntry(JournalEntriesWithDb journalEntry){
        // Adding business logic: Automatically set the current server time before saving
        journalEntry.setDate(LocalDateTime.now());
        journalRepository.save(journalEntry);
    }

    @Override
    public List<JournalEntriesWithDb> getAll(){
        return journalRepository.findAll();
    }

    @Override
    public Optional<JournalEntriesWithDb> getById(String id){
        // Safety check: Ensure the string is a valid MongoDB ObjectId format
        if(ObjectId.isValid(id)){
            return journalRepository.findById(new ObjectId(id));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(String id){
        if(ObjectId.isValid(id)){
            journalRepository.deleteById(new ObjectId(id));
            return true;
        }
        return false;
    }

    @Override
    public JournalEntriesWithDb putById(String id, JournalEntriesWithDb newJournalEntry){
        // Logic for "Partial Updates": 
        // 1. Find existing record.
        // 2. If new data is provided, overwrite; otherwise, keep the old data.
        JournalEntriesWithDb oldJournalEntry = journalRepository.findById(new ObjectId(id)).orElse(null);
        
        if(ObjectId.isValid(id) && oldJournalEntry != null){
            // Check if name is provided and not empty
            oldJournalEntry.setName(newJournalEntry.getName() != null && !newJournalEntry.getName().equals("") 
                ? newJournalEntry.getName() : oldJournalEntry.getName());
            
            // Check if content is provided and not empty
            oldJournalEntry.setContent(newJournalEntry.getContent() != null && !newJournalEntry.getContent().equals("") 
                ? newJournalEntry.getContent() : oldJournalEntry.getContent());

            return journalRepository.save(oldJournalEntry); // Save the merged object
        }
        return null;
    }
}