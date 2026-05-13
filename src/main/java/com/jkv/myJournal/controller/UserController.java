package com.jkv.myJournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/Users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(){
        if(!userService.getAll().isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("no data");    
    }

    @PostMapping
    public ResponseEntity<?> saveUsers(@RequestBody UserEntity userEntity) {
        if(userEntity!=null){
            userService.saveAll(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(userEntity);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body can't be null");
    }   
    
    @GetMapping({"/id/{myId}","/id"})
    public ResponseEntity<?> getUserById(@PathVariable(required = false) String myId){
        if(myId!=null && !myId.equals("")){
            return ResponseEntity.status(HttpStatus.OK).body(userService.getById(myId));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    }

    @PutMapping({"id/{myId}","/id"})
    public ResponseEntity<?> updateUserById(@PathVariable(required = false) String myId, @RequestBody UserEntity newUser) {
        if(myId!=null && !myId.equals("")){
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(myId,newUser));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    }

    @DeleteMapping({"id/{myId}","/id"})
    public ResponseEntity<?> deleteUserById(@PathVariable(required = false) String myId){
        if(myId!=null && !myId.equals("")){
            return ResponseEntity.status(HttpStatus.OK).body(userService.deleteById(myId));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    }

    @PutMapping({"/{userName}","/"})
    public ResponseEntity<?> putMethodName(@PathVariable(required = false) String userName, @RequestBody UserEntity newUser) {
        if(userName!=null){
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateByName(userName, newUser));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userName not valid or null or empty");
    }
}
