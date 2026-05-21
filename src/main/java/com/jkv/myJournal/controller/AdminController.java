package com.jkv.myJournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/view-users")
    public ResponseEntity<?> viewAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }

    @PostMapping("/create-admins")
    public ResponseEntity<?> createAdmins(@RequestBody(required = false) UserEntity userEntity){
        if(userEntity!=null){
            userService.saveNewAdmin(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(userEntity);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("body can't be null or empty");  
    }
}
