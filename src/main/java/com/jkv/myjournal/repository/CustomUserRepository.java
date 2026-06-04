package com.jkv.myjournal.repository;

import java.util.List;

import com.jkv.myjournal.entity.UserEntity;

public interface CustomUserRepository {
    List<UserEntity> findUsersByCityAndRole(String role, String city);
}
