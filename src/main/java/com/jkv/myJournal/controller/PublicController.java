package com.jkv.myJournal.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "test-running!";
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> saveNewUsers(@RequestBody UserEntity userEntity) {
        if(userEntity!=null){
            if(userService.saveNewAll(userEntity))
                return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);
            else{
                log.warn("Attempted to register duplicate username: {}", userEntity.getUserName());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate UserName");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body can't be null");
    }
}
