package com.challenge.wallet.service;

import com.challenge.wallet.client.UserClient;
import com.challenge.wallet.dto.wallet.UserResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class UserService {

    private UserClient userClient;

    @Autowired
    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public boolean isUserValid(UUID userId) {
        log.info("Processing whether the user is valid.");
        ResponseEntity<UserResponse> response = userClient.getById(userId);

        log.info("HttpStatus of the user validation request: {}, hasBody: {}", response.getStatusCode(), response.hasBody());

        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new RuntimeException("Could not find the user with the following id:" + userId);
        } else {
            return response.hasBody();
        }
    }
}
