package com.jkv.myjournal.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jkv.myjournal.entity.JournalEntityWithDb;
import com.jkv.myjournal.entity.UserEntity;
import com.jkv.myjournal.event.EmailEvent;
import com.jkv.myjournal.repository.JournalRepository;
import com.jkv.myjournal.repository.UserRepository;
import com.jkv.myjournal.service.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final JournalRepository journalRepository;
    /**
     * By putting @Autowired directly on top of the private PasswordEncoder passwordEncoder; variable
     * Spring grabs your BCryptPasswordEncoder bean and drops it directly into that variable.
     * 
     * from
     * @Bean
     * public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     */
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Why @NonNull is used here:
     * 1. Spring Data Repositories (userRepository) are strictly typed to not accept nulls.
     * 2. Since this method simply "passes through" the userEntity to the repository, 
     *    the IDE sees a risk: "What if userEntity is null?"
     * 3. Lombok's @NonNull adds a hidden 'if (userEntity == null) throw NPE' check 
     *    at the start. This "proves" to the IDE that the value is safe before it 
     *    reaches userRepository.save(), thus clearing the warning.
     */
    /**
     * Alternative - if we can check object is not null before passing to save method, it doesn't give any warning.
        @Override
        public void saveAll(UserEntity userEntity) {
            if(userEntity!=null)
            userRepository.save(userEntity);
    }
     */
    @Override
    @CacheEvict(value = "all_users_cache", key="'allUsers'")
    public boolean saveNewAll(@NonNull UserEntity userEntity) {
        try{
            String newUserName = userEntity.getUserName();
            UserEntity existingUser = userRepository.getByUserName(newUserName);
            if(existingUser!=null){
                // return false;
            }
            userEntity.setUserPassword(passwordEncoder.encode(userEntity.getUserPassword()));
            userEntity.setRoles(Arrays.asList("USER"));
            userRepository.save(userEntity);
            return true;
        }
        catch(Exception e){
            logger.error("error occured for {}",userEntity.getUserName(),e);
            logger.trace("trace the error");
            logger.debug("debug the error");
            logger.warn("warning! error occured!");
            logger.info("info about error");
            /**
             * Logging levels- Trace < debug < info < warn < error  -- this severity level is followed respectively.
             * 
             * by default logging is enabled for info, warn, error only. That is why trace and debug is not working in above example. 
             */

            return false;
        }
    }
    
    @Override
    @Transactional
    /*
    here the transactional feature doesn't work because the emailService is set as Async.
    so here once the user got created the emailService hand over to a new thread which the transactional thinks like the main thread task is completed.
    inorder to make the transactional work we have remove the Async method from method.


    even if you remove the @Async from the sendEmail method in EmailServiceImpl, the transactional will not work.
    because all the error are caught using catch so there are no unhandled error to trigger the rollback process.
     */
    @CacheEvict(value = "all_users_cache", key="'allUsers'")
    public void saveNewAdmin(@NonNull UserEntity userEntity) {
        userEntity.setUserPassword(passwordEncoder.encode(userEntity.getUserPassword()));
        userEntity.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(userEntity);
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("New Admin has been created in MyJournal application.\n")
                .append("UserName: ").append(userEntity.getUserName()).append("\n")
                .append("Please check activity in MongoDB Atlas.\n\n")
                .append("Thank you,\n")
                .append("ADMIN");

        EmailEvent event = EmailEvent.builder()
        .to("jkv9963@gmail.com")
        .subject("New Admin created.")
        .body(emailBody.toString())
        .build();
        logger.info("Publishing admin creation event to Kafka for user: {}", userEntity.getUserName());
        kafkaTemplate.send("email-notification-events",userEntity.getUserName(),event);
    }
    
    @Override
    public void saveUser(@NonNull UserEntity userEntity){
        userRepository.save(userEntity);
    }

    @Override
    @Cacheable(value = "all_users_cache", key="'allUsers'")
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Cacheable(value="singleUser",key="#id")
    public Optional<UserEntity> getById(String id) {
        if(ObjectId.isValid(id)){
            return userRepository.findById(new ObjectId(id));
        }
        return Optional.empty();
    }

    @Override
    @CacheEvict(value="singleUser",key="#id")
    public boolean deleteById(String id) {
        if(ObjectId.isValid(id)){
            userRepository.deleteById(new ObjectId(id));
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value="singleUser",key="#id")
    public boolean updateById(String id, UserEntity newUser) {
        UserEntity oldUser = userRepository.findById(new ObjectId(id)).orElse(null);
        if(ObjectId.isValid(id)&& oldUser!=null){
            oldUser.setUserName((!newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword(passwordEncoder.encode((!newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword()));
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value="singleUser",key="#userName")
    public boolean updateByName(String userName, UserEntity newUser) {
        UserEntity oldUser = userRepository.getByUserName(userName);
        if(oldUser!=null){
            oldUser.setUserName((!newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword(passwordEncoder.encode((!newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword()));
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    @CacheEvict(value="singleUser",key="#userName")
    public boolean deleteByName(String userName) {
        UserEntity user = userRepository.getByUserName(userName);
        if(user == null){
            return false;
        }
        List<ObjectId> journalIds = user.getJournalEntries().stream()
        .map(JournalEntityWithDb::getId) //The Method Reference Way (ClassName::methodName)
        .collect(Collectors.toList());
        if(!journalIds.isEmpty()){
            journalRepository.deleteByIdIn(journalIds);
        }
        userRepository.delete(user);
        return true;
    }

    @Override
    @Cacheable(value="singleUser",key="#userName")
    public UserEntity getByUserName(String userName) {
        return userRepository.getByUserName(userName);
    }

    @Override
    public String getCity(String userName){
        return userRepository.getByUserName(userName).getCity();
    }

    @Override
    public List<UserEntity> getUsersByCityAndRole(String role,String city){
        return userRepository.findUsersByCityAndRole(role, city);
    } 
}
