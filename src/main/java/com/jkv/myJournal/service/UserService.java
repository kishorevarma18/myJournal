package com.jkv.myJournal.service;

import java.util.List;
import java.util.Optional;

import com.jkv.myJournal.entity.UserEntity;

public interface UserService {
    void saveAll(UserEntity userEntity);
    List<UserEntity> getAll();
    Optional<UserEntity> getById(String id);
    Boolean deleteById(String id);
    Boolean updateById(String id, UserEntity userEntity);
    Boolean updateByName(String name, UserEntity userEntity);
}
