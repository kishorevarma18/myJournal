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
    @Override
    public void saveAll(@NonNull UserEntity userEntity) {
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
    public Boolean updateByName(String name, UserEntity newUser) {
        UserEntity oldUser = userRepository.getByUserName(name);
        if(oldUser!=null){
            oldUser.setUserName((newUser.getUserName()!=null && !newUser.getUserName().equals(""))?newUser.getUserName():oldUser.getUserName());
            oldUser.setUserPassword((newUser.getUserPassword()!=null && !newUser.getUserPassword().equals(""))?newUser.getUserPassword():oldUser.getUserPassword());
            userRepository.save(oldUser);
            return true;
        }
        return false;
    }
}
