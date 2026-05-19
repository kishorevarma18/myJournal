package com.jkv.myJournal.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jkv.myJournal.entity.UserEntity;
import com.jkv.myJournal.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    // @GetMapping
    // public ResponseEntity<?> getUsers(){
    //     if(!userService.getAll().isEmpty())
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).body("no data");    
    // }   
    
    // @GetMapping({"/id/{myId}","/id"})
    // public ResponseEntity<?> getUserById(@PathVariable(required = false) String myId){
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.getById(myId));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    // @PutMapping({"id/{myId}","/id"})
    // public ResponseEntity<?> updateUserById(@PathVariable(required = false) String myId, @RequestBody UserEntity newUser) {
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(myId,newUser));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    // @DeleteMapping({"id/{myId}","/id"})
    // public ResponseEntity<?> deleteUserById(@PathVariable(required = false) String myId){
    //     if(myId!=null && !myId.equals("")){
    //         return ResponseEntity.status(HttpStatus.OK).body(userService.deleteById(myId));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not available or null or empty");
    // }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserEntity newUser) {
        Authentication authenticatedUser=SecurityContextHolder.getContext().getAuthentication();
        String userName = authenticatedUser.getName();
        if(userName!=null){
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateByName(userName, newUser));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userName not valid or null or empty");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Principal principal){
        String userName = principal.getName();
        try{
                return ResponseEntity.status(HttpStatus.OK).body(userService.deleteByName(userName));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); 
        }
        
    }
}
