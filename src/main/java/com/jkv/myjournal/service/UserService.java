package com.jkv.myjournal.service;

import java.util.List;
import java.util.Optional;

import com.jkv.myjournal.entity.UserEntity;

public interface UserService {
    boolean saveNewAll(UserEntity userEntity);
    void saveUser(UserEntity userEntity);
    void saveNewAdmin(UserEntity userEntity);
    List<UserEntity> getAll();
    Optional<UserEntity> getById(String id);
    boolean deleteById(String id);
    boolean updateById(String id, UserEntity userEntity);
    boolean updateByName(String userName, UserEntity userEntity);
    UserEntity getByUserName(String userName);
    boolean deleteByName(String userName);
    String getCity(String userName);
    List<UserEntity> getUsersByCityAndRole(String role,String city);
}
