package com.jkv.myjournal.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jkv.myjournal.entity.JournalEntityWithDb;

/**
 * @Repository tells Spring to manage this interface as a bean. 
 * It acts as the Data Access Layer of your application.
 */
@Repository
public interface JournalRepository extends MongoRepository<JournalEntityWithDb, ObjectId> {
    
    /* 
       By extending MongoRepository, you inherit a full suite of CRUD operations 
       without writing a single line of implementation code:
       
       1. save(entity): Saves or updates a journal entry.
       2. findById(id): Retrieves an entry by its ObjectId.
       3. findAll(): Returns a List of all entries in the 'journal_entries' collection.
       4. deleteById(id): Removes an entry from the database.
       5. count(): Returns the total number of documents in the collection.
    */

    void deleteByIdIn(List<ObjectId> ids);
    /*
       Custom derived delete query based on Spring Data naming conventions.
       
       - "deleteBy": Tells Spring Data to generate a deletion query.
       - "Id": Targets the internal primary key field ("_id") of the document.
       - "In": Instructs Spring Data to expect a collection/list of values.
       
       Behind the scenes, Spring parses this method name and automatically maps 
       the List<ObjectId> to MongoDB's native "$in" query operator. 
       
       Database equivalent: db.journal_entries.deleteMany({ _id: { $in: [id1, id2, ...] } })
       
       @param ids The list of BSON ObjectIds identifying the journals to be batch-deleted.
    */
}