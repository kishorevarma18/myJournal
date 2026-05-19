package com.jkv.myJournal.service;

import java.util.List;
import java.util.Optional;

import com.jkv.myJournal.entity.UserEntity;

public interface UserService {
    void saveAll(UserEntity userEntity);
    List<UserEntity> getAll();
    Optional<UserEntity> getById(String id);
    boolean deleteById(String id);
    boolean updateById(String id, UserEntity userEntity);
    boolean updateByName(String userName, UserEntity userEntity);
    UserEntity getByUserName(String userName);
    boolean deleteByName(String userName);
}
