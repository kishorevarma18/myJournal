package com.jkv.myJournal.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.jkv.myJournal.entity.UserEntity;

/**
 * @Repository: Tells Spring this is a Data Access Object (DAO). 
 * While optional for MongoRepository, it helps with exception translation.
 */
@Repository 
public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {

    /**
     * WHY create custom methods:
     * 1. Default methods (findById) only work with the Primary Key (_id).
     * 2. In your app, you often identify users by their 'userName', not their random ObjectId.
     * 3. This allows you to fetch a user directly using a human-readable unique field.
     *
     * HOW it works (Query Derivation):
     * Spring Data MongoDB uses "Method Name Parsing." It looks at the name 'getByUserName':
     * - 'get' or 'find' or 'read': Tells Spring this is a SELECT query.
     * - 'By': Acts as a delimiter.
     * - 'UserName': Tells Spring to look for a field named 'userName' in your UserEntity.
     * 
     * Spring automatically generates the MongoDB query: db.users.find({ "userName": "..." })
     */
    UserEntity getByUserName(String userName);

    // Spring Data Mongo's deleteBy returns a Long (deleted count), not a boolean.
    // Using boolean causes a ClassCastException because Mongo can't auto-cast Long to Boolean.
    Long deleteByUserName(String userName);
}