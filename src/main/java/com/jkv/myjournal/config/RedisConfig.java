package com.jkv.myjournal.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuration class to set up Redis Caching infrastructure for the myJournal application.
 * Defines the operational rules, expiration boundaries, and data serialization engines.
 */
@Configuration
@EnableCaching // Globally activates Spring's declarative caching capabilities (@Cacheable, @CacheEvict, @CachePut)
public class RedisConfig {

    /**
     * Constructs and registers a customized RedisCacheManager bean.
     * This manager dictates how Spring Cache annotations interact with the Redis store,
     * translating abstract Java collections into structural JSON entries securely.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        /*
         * Registers support for Java 8 date/time types (like LocalDateTime, LocalDate).
         * Without this module, Jackson fails to serialize/deserialize standard native timestamps
         * attached to journal entries, leading to application deserialization runtime crashes.
         */
        objectMapper.registerModule(new JavaTimeModule());
        
        /*
         * Enables Default Typing to append class metadata directly into the generated JSON string.
         * This preserves structural type integrity for polymorphic objects and interfaces, ensuring 
         * that abstract fields or nested subclasses are rebuilt accurately upon reading from Redis.
         */
        objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        /*
         * Binds an abstraction MixIn interface to MongoDB's third-party ObjectId class.
         * This forces Jackson to ignore internal, asymmetric getter fields (such as 'getDate()') 
         * inside ObjectId instances, preventing structural read exceptions when restoring cached 
         * database entries from Redis payloads.
         */
        objectMapper.addMixIn(org.bson.types.ObjectId.class, ObjectIdMixIn.class);
        
        /*
         * Wraps the configured ObjectMapper instance inside a generic JSON serializer.
         * This maps standard Java Entity objects seamlessly into readable, human-inspectable JSON strings 
         * inside the Redis server instead of cryptic, unreadable binary streams.
         */
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        /*
        ***NOTE***-
        all this ObjectMapper can be avoided if declare id as String instead of ObjectId.
        MongoDb is smart enough to convert String to Bson ObjectId if we mention @Id on top of it.
        so we can use it in 2 ways-
        @Id
        private String id;
        
        or
        
        @Id
        @Field(targetField=FieldType.OBJECT_ID)
        private String id;
        */

        /*
         * Establishes the default behavior rules for the caching engine namespace:
         * 1. entryTtl: Restricts cache lifetimes to 10 minutes to protect against prolonged stale data.
         * 2. disableCachingNullValues: Stops empty database results from occupying memory spaces.
         * 3. serializeKeysWith: Formats cache namespace keys cleanly into explicit string formats.
         * 4. serializeValuesWith: Applies the custom, object-preserving Jackson serializer for values.
         */
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        
        // Builds and seals the CacheManager around the preconfigured network factory and serialization rules
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }

    /**
     * Declares a customized RedisTemplate bean designed for manual programmatic cache interaction.
     * Maps plain String keys to generic Java Objects, preventing standard JDK binary serialization 
     * by automatically converting Java objects to structured JSON payloads. This design makes it 
     * fully compatible with generic utility helper engines like custom RedisService layers.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        
        // Links the abstraction template directly to the active database connection channel
        template.setConnectionFactory(factory);
        
        // Formats programmatic key inputs into clear alphanumeric strings inside the Redis server
        template.setKeySerializer(new StringRedisSerializer());
        
        // Intercepts Java object payloads and automatically serializes them into structured JSON text
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }
}