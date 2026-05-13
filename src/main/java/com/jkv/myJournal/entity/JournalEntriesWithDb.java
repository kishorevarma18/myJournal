package com.jkv.myJournal.entity;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/*
The Entity is the "suitcase" that carries the data through every single layer.
Without the Entity, the layers would have nothing to pass to one another.
*/

import lombok.Data;
// import lombok.Getter;
// import lombok.Setter;


/**
 * @Document identifies this class as a domain object to be persisted to MongoDB.
 * The 'collection' attribute specifies the name of the MongoDB collection 
 * where these objects will be stored.
 */
@Document(collection = "journal_entries")
/**
 * @Getter and @Setter: These generate the standard getField() and setField() 
 * methods for all fields in the class at compile-time.
 */
// @Getter@Setter
/**
 * @Data: The "All-In-One" Lombok power-tool.
 * It automatically triggers:
 * 1. @Getter and @Setter (making your manual @Getter/@Setter annotations above redundant).
 * 2. @ToString (allows you to print the object and see its actual data).
 * 3. @EqualsAndHashCode (useful for comparing two objects).
 * 4. @RequiredArgsConstructor (creates a constructor for any final fields).
 */
@Data
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
}