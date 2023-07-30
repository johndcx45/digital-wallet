package com.challenge.wallet.service;

import com.challenge.wallet.client.UserClient;
import com.challenge.wallet.dto.wallet.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {
    private static WireMockServer wireMockServer;
    private UserService userService;
    @Autowired
    private UserClient userClient;
    private UserResponse userResponse;
    private ObjectMapper objectMapper;
    private UUID userId;
    private UUID walletId;

    @BeforeAll
    static void init() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @BeforeEach
    void setup() {
        userService = new UserService(userClient);
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
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/users/44ae5fe1-bf3f-49bb-8c84-965db4552a57")).willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(userResponse))
                )
        );

        boolean response = userService.isUserValid(UUID.fromString("44ae5fe1-bf3f-49bb-8c84-965db4552a57"));

        Assertions.assertThat(response).isEqualTo(true);
    }

    @Test
    public void givenUserInvalid_whenIsUserValid_thenReturnTrue() throws JsonProcessingException {
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/users/44ae5fe1-bf3f-49bb-8c84-965db4552a57")).willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(objectMapper.writeValueAsString(userResponse))
                )
        );

        Assertions.assertThatThrownBy(() -> {
            userService.isUserValid(UUID.fromString("44ae5fe1-bf3f-49bb-8c84-965db4552a57"));
        }).isInstanceOf(RuntimeException.class);
    }
}