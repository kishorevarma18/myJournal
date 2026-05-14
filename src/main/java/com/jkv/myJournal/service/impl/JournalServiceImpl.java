package com.jkv.myJournal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jkv.myJournal.entity.JournalEntityWithDb;
import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.JournalRepository;
import com.jkv.myJournal.service.JournalService;
import com.jkv.myJournal.service.UserService;

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

    @Autowired
    private UserService userService;

    @Override
    public void saveEntry(JournalEntityWithDb journalEntry, String userName){
        // Adding business logic: Automatically set the current server time before saving
        UserEntity user = userService.getByUserName(userName);
        if(user == null)
            throw new RuntimeException("User not found with username: "+userName);
        journalEntry.setDate(LocalDateTime.now());
        JournalEntityWithDb saved = journalRepository.save(journalEntry);
        user.getJournalEntries().add(saved);
        userService.saveAll(user);
    }

    @Override
    public List<JournalEntityWithDb> getAll(String userName){
        UserEntity user = userService.getByUserName(userName);
        if(user == null)
            throw new RuntimeException("User not found with username: "+userName);
        return user.getJournalEntries();
    }

    @Override
    public Optional<JournalEntityWithDb> getById(String id){
        // Safety check: Ensure the string is a valid MongoDB ObjectId format
        if(ObjectId.isValid(id)){
            return journalRepository.findById(new ObjectId(id));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(String id,String userName){
        UserEntity user = userService.getByUserName(userName);
        if(!ObjectId.isValid(id)){
            return false;
        }
        if(user == null){
            throw new RuntimeException("user not found with username: "+userName);
        }
        journalRepository.deleteById(new ObjectId(id));
        user.getJournalEntries().removeIf(entry->entry.getId().equals(new ObjectId(id)));
        userService.saveAll(user);
        return true;
    }

    @Override
    public JournalEntityWithDb putById(String id, JournalEntityWithDb newJournalEntry, String userName){
        // Logic for "Partial Updates": 
        // 1. Find existing record.
        // 2. If new data is provided, overwrite; otherwise, keep the old data.
        JournalEntityWithDb oldJournalEntry = journalRepository.findById(new ObjectId(id)).orElse(null);
        UserEntity user = userService.getByUserName(userName);
        if(user == null){
            throw new RuntimeException("user not found for username: "+userName);
        }
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