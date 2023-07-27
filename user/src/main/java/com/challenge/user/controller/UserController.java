package com.challenge.user.controller;

import com.challenge.user.domain.User;
import com.challenge.user.dto.UserCreateRequest;
import com.challenge.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
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
        User user = userService.findById(id);

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);

        return new ResponseEntity(HttpStatus.CREATED);
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
