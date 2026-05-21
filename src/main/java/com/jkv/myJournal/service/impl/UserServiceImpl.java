package com.jkv.myJournal.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jkv.myJournal.entity.JournalEntityWithDb;
import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.JournalRepository;
import com.jkv.myJournal.repository.UserRepository;
import com.jkv.myJournal.service.UserService;

import lombok.NonNull;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalRepository journalRepository;

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
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void saveNewAll(@NonNull UserEntity userEntity) {
        userEntity.setUserPassword(passwordEncoder.encode(userEntity.getUserPassword()));
        userEntity.setRoles(Arrays.asList("USER"));
        userRepository.save(userEntity);
    }
    
    @Override
    public void saveNewAdmin(@NonNull UserEntity userEntity) {
        userEntity.setUserPassword(passwordEncoder.encode(userEntity.getUserPassword()));
        userEntity.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(userEntity);
    }
    
    @Override
    public void saveUser(@NonNull UserEntity userEntity){
        userRepository.save(userEntity);
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> getById(String id) {
        if(ObjectId.isValid(id)){
            return userRepository.findById(new ObjectId(id));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(String id) {
        if(ObjectId.isValid(id)){
            userRepository.deleteById(new ObjectId(id));
            return true;
        }
        return false;
    }

    @Override
    public boolean updateById(String id, UserEntity newUser) {
        UserEntity oldUser = userRepository.findById(new ObjectId(id)).orElse(null);
        if(ObjectId.isValid(id)){
            oldUser.setUserName((newUser.getUserName()!=null && !newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword(passwordEncoder.encode((newUser.getUserPassword()!=null && !newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword()));
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByName(String userName, UserEntity newUser) {
        UserEntity oldUser = userRepository.getByUserName(userName);
        if(oldUser!=null){
            oldUser.setUserName((newUser.getUserName()!=null && !newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword(passwordEncoder.encode((newUser.getUserPassword()!=null && !newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword()));
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
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
    public UserEntity getByUserName(String userName) {
        return userRepository.getByUserName(userName);
    }
}
