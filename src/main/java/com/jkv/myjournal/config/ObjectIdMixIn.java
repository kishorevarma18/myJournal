package com.jkv.myjournal.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Jackson MixIn used to customize how third-party classes are serialized.
 * This interface acts as a "decorator" for MongoDB's 'ObjectId' class, 
 * changing how Jackson handles its fields without modifying the actual MongoDB source code.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Tells Jackson to silently ignore any unexpected or unmapped internal getters/properties inside ObjectId
public interface ObjectIdMixIn {

}