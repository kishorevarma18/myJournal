package com.jkv.myJournal.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.repository.UserRepository;
import com.jkv.myJournal.service.UserService;

import lombok.NonNull;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    /**
     * Why @NonNull is used here:
     * 1. Spring Data Repositories (userRepository) are strictly typed to not accept nulls.
     * 2. Since this method simply "passes through" the userEntity to the repository, 
     *    the IDE sees a risk: "What if userEntity is null?"
     * 3. Lombok's @NonNull adds a hidden 'if (userEntity == null) throw NPE' check 
     *    at the start. This "proves" to the IDE that the value is safe before it 
     *    reaches userRepository.save(), thus clearing the warning.
     */
    @Override
    public void saveAll(@NonNull UserEntity userEntity) {
        userRepository.save(userEntity);
    }
    // Alternative - if we can check object is not null before passing to save method, it doesn't give any warning.
    // @Override
    // public void saveAll(UserEntity userEntity) {
    //     if(userEntity!=null)
    //     userRepository.save(userEntity);
    // }

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
    public Boolean deleteById(String id) {
        if(ObjectId.isValid(id)){
            userRepository.deleteById(new ObjectId(id));
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateById(String id, UserEntity newUser) {
        UserEntity oldUser = userRepository.findById(new ObjectId(id)).orElse(null);
        if(ObjectId.isValid(id)){
            oldUser.setUserName((newUser.getUserName()!=null && !newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword((newUser.getUserPassword()!=null && !newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword());
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateByName(String userName, UserEntity newUser) {
        UserEntity oldUser = userRepository.getByUserName(userName);
        if(oldUser!=null){
            oldUser.setUserName((newUser.getUserName()!=null && !newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword((newUser.getUserPassword()!=null && !newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword());
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }

    @Override
    public UserEntity getByUserName(String userName) {
        return userRepository.getByUserName(userName);
    }
}
