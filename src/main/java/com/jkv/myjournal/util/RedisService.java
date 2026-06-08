package com.jkv.myjournal.util;


// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    
    // Configured with String keys and Object values, linked to GenericJackson2JsonRedisSerializer
    private final RedisTemplate<String, Object> redisTemplate;
    //private ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Saves a Java object into Redis with a Time-To-Live (TTL) expiration.
     */
    public void set(String key,Object o,Long ttl){
        //String jsonValue;
        try {
            //jsonValue = mapper.writeValueAsString(o);
            
            /*
             * .opsForValue() (Operations for Value): 
             * Tells Spring we want to work with standard Redis "String" key-value pairs.
             * * .set(key, o, ttl, TimeUnit.SECONDS):
             * Since our template value is 'Object', we pass the Java object 'o' directly. 
             * The template's ValueSerializer intercepts 'o' and converts it to a JSON string 
             * automatically before writing it to Redis with the specified expiration time.
             */
            redisTemplate.opsForValue().set(key,o,ttl,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception while converting Object to string for key {} ",key, e);
        }
    }

    /**
     * Removes a key-value pair from the Redis store entirely.
     */
    public void delete(String key){
        redisTemplate.delete(key);
    }

    /**
     * Fetches data from Redis by its key and automatically reconstructs it into a Java Object.
     */
    public Object get(String key){
        try {
            /*
             * .opsForValue().get(key):
             * Fetches the raw JSON string stored under the key in Redis.
             * The template automatically uses its serializer to read the JSON and rebuild 
             * it into its original Java object type using the stored class metadata.
             */
            Object o=redisTemplate.opsForValue().get(key);
            if(o==null){
                return null;
            }          
            // return mapper.readValue(o.toString(), entityclass);
            return o;
        } catch (Exception e) {
            log.error("Exception while converting String to Object for key {}: ",key,e);
            return null;
        } 
    }
}