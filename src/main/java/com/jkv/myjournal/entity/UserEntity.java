package com.jkv.myjournal.entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;

@Document(collection="users") // Maps this class to the "users" collection in MongoDB
@Data // Lombok: Automatically generates Getters, Setters, toString, equals, and hashCode
public class UserEntity {

    @Id // Marks this field as the primary key (_id) in MongoDB
    private ObjectId id;

    /**
     * @Indexed(unique = true)
     * -----------------------
     * 1. Performance: Tells MongoDB to create an index on this field, making searches by 'userName' much faster.
     * 2. Constraint: The 'unique = true' part ensures that no two documents in the collection can have the same username.
     * Note: For this to work automatically, ensure 'auto-index-creation' is set to true in your application.properties.
     */
    @Indexed(unique = true)
    @NonNull 
    private String userName;

    /**
     * @NonNull (Lombok)
     * -----------------
     * 1. Null Safety: Generates a null-check in the constructor and setter methods.
     * 2. Runtime Exception: If you try to set this field to null, it will throw a NullPointerException immediately.
     * 3. Documentation: Clearly signals to other developers that this field is mandatory.
     */
    @NonNull
    private String userPassword;

    /**
     * @DBRef
     * -------
     * 1. Referencing: Instead of nesting the entire journal object inside the User document, 
     *    MongoDB will only store the 'ObjectIDs' of the journals.
     * 2. Linking: When you fetch a User, Spring Data MongoDB uses these IDs to automatically 
     *    fetch the corresponding documents from the "journal_entries" collection.
     * 3. Scalability: This keeps the User document small and prevents it from hitting 
     *    the 16MB BSON document limit if a user has thousands of entries.
     */
    @DBRef
    private List<JournalEntityWithDb> journalEntries = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
}