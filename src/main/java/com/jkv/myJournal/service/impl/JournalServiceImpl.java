package com.jkv.myJournal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void saveEntry(JournalEntityWithDb journalEntry, String userName){
        /*
        1. TRANSACTION START:
        When this method begins, Spring starts a 'Session' with MongoDB.
        Every database operation from here until the end of the method 
        is considered a single "Unit of Work."

        Adding business logic: Automatically set the current server time before saving
        */
        UserEntity user = userService.getByUserName(userName);
        journalEntry.setDate(LocalDateTime.now()); 
        /*
        since the above method checks for null and doesn't allow null value. we don't have to explicitly provide @NonNull.
        
        2. PARTIAL SUCCESS:
        This saves the Journal Entry to the 'journal_entries' collection.
        Without @Transactional, this would be permanently saved right now.
        */
        JournalEntityWithDb saved = journalRepository.save(journalEntry);
        /* 
        CRITICAL SCENARIO: If you uncomment the line below:
        user.setUserName(null); 
        
        This will trigger a "Duplicate Key Error" (or a Null constraint error) 
        when 'userService.saveAll(user)' is called below. 
        */
        user.getJournalEntries().add(saved);
        /*
        3. THE FAIL POINT:
        If this save fails (due to the null username or a network error),
        an Exception is thrown.
        */
        userService.saveUser(user);
        /*
        4. THE ROLLBACK (How @Transactional prevents inconsistency):
        Because this method is @Transactional, Spring catches the exception.
        It tells MongoDB: "Abort! Undo everything!"
        The Journal Entry saved in Step 2 is DELETED from the database.
        Result: Your data stays consistent. You don't have "orphan" journal entries 
        that aren't linked to a user.
        */
    }

    @Override
    public List<JournalEntityWithDb> getAll(String userName){
        UserEntity user = userService.getByUserName(userName);
        return user.getJournalEntries();
    }

    @Override
    public Optional<JournalEntityWithDb> getById(String id, String userName){
        // Safety check: Ensure the string is a valid MongoDB ObjectId format
        if(ObjectId.isValid(id)){
            UserEntity user = userService.getByUserName(userName);
            return user.getJournalEntries().stream().filter(x->x.getId().equals(new ObjectId(id))).findFirst();
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public boolean deleteById(String id,String userName){
        UserEntity user = userService.getByUserName(userName);
        if(user.getJournalEntries().removeIf(entry->entry.getId().equals(new ObjectId(id))) && ObjectId.isValid(id)){
            journalRepository.deleteById(new ObjectId(id));
            userService.saveUser(user);
            return true;
        }
        return false;
    }

    @Override
    public JournalEntityWithDb putById(String id, JournalEntityWithDb newJournalEntry, String userName){
        /*
        Logic for "Partial Updates": 
        1. Find existing record.
        2. If new data is provided, overwrite; otherwise, keep the old data.
        */
        
        UserEntity user = userService.getByUserName(userName);
        JournalEntityWithDb oldJournalEntry = user.getJournalEntries().stream().filter(x->x.getId().equals(new ObjectId(id))).findFirst().orElse(null);
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