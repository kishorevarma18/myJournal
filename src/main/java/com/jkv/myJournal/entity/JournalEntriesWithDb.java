package com.jkv.myJournal.entity;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Document identifies this class as a domain object to be persisted to MongoDB.
 * The 'collection' attribute specifies the name of the MongoDB collection 
 * where these objects will be stored.
 */
@Document(collection = "journal_entries")
public class JournalEntriesWithDb {
    /**
     * @Id designates this field as the primary key.
     * Spring Data maps this 'id' field to the '_id' field in the MongoDB document.
     */
    @Id // Explicitly marks this as the primary key
    private ObjectId id;
    private String name;
    private String content;
    private LocalDateTime date;
    //getters
    public ObjectId getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getDate() {
        return date;
    }
    //setters
    public void setId(ObjectId id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
