package com.jkv.myjournal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jkv.myjournal.entity.UserEntity;
import com.jkv.myjournal.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/view-all-users")
    public ResponseEntity<List<UserEntity>> viewAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }

    @PostMapping("/create-admins")
    public ResponseEntity<Object> createAdmins(@RequestBody(required = false) UserEntity userEntity){
        if(userEntity!=null){
            userService.saveNewAdmin(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(userEntity);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("body can't be null or empty");  
    }

    @GetMapping("/view-users-by-filter")
    public ResponseEntity<Object> viewUsersByFilter(@RequestParam String role,@RequestParam String city){
        List<UserEntity> list = userService.getUsersByCityAndRole(role, city);
        if(!list.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no users based on the filter!");
        }
    }
}
