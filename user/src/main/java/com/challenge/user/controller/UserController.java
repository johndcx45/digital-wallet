package com.challenge.user.controller;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;
import com.challenge.user.dto.UpdateUserRequest;
import com.challenge.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> get(@RequestParam("page") @Min(0) Integer page,
                                          @RequestParam("size") @Min(1) Integer size) {
        List<User> users = userService.findWithPagination(page, size);
        if(users == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") UUID id) {
        Optional<User> user = userService.findById(id);

        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);

        if(user == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.updateUser(updateUserRequest);

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity remove(@PathVariable("id") UUID id) {
        try {
            userService.deleteById(id);

            return new ResponseEntity(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
