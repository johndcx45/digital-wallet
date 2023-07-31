package com.challenge.wallet.service;

import com.challenge.wallet.client.UserClient;
import com.challenge.wallet.dto.wallet.UserResponse;
import com.challenge.wallet.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userService;
    @Mock
    private UserClient userClient;
    private UserResponse userResponse;
    private ObjectMapper objectMapper;
    private UUID userId;
    private UUID walletId;

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userClient);
        userId = UUID.fromString("44ae5fe1-bf3f-49bb-8c84-965db4552a57");
        walletId = UUID.fromString("0abd1738-6bee-4ae4-a32c-41c1bd3bdd82");
        userResponse = new UserResponse(
                userId,
                "username-1",
                "First Name",
                "12345678900",
                "Last Name",
                new Date(),
                walletId
        );
        objectMapper = new ObjectMapper();
    }

    @Test
    public void givenUserValid_whenIsUserValid_thenReturnTrue() throws JsonProcessingException {
        when(userClient.getById(Mockito.any(UUID.class))).thenReturn(new ResponseEntity<>(userResponse,HttpStatus.OK));

        boolean response = userService.isUserValid(UUID.fromString("44ae5fe1-bf3f-49bb-8c84-965db4552a57"));

        Assertions.assertThat(response).isEqualTo(true);
    }

    @Test
    public void givenUserInvalid_whenIsUserValid_thenReturnTrue() throws JsonProcessingException {
        when(userClient.getById(Mockito.any(UUID.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Assertions.assertThatThrownBy(() -> {
            userService.isUserValid(UUID.fromString("44ae5fe1-bf3f-49bb-8c84-965db4552a57"));
        }).isInstanceOf(RuntimeException.class);
    }
}