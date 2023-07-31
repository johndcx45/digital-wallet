package com.challenge.user.service.impl;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;
import com.challenge.user.dto.UpdateUserRequest;
import com.challenge.user.dto.WalletCreatedResponse;
import com.challenge.user.repository.UserRepository;
import com.challenge.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper objectMapper;

    public UserServiceImpl(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<User> findWithPagination(Integer page, Integer size) {
        if(page < 0 || size < 0) {
            throw new RuntimeException("One of the parameters is invalid.");
        }
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));

        if(users.getTotalElements() == 0) {
            return null;
        }

        return users.getContent();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(CreateUserRequest createUserRequest) {

        User user = User.builder()
                .createdAt(new Date())
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .socialSecurityNumber(createUserRequest.getSocialSecurityNumber())
                .username(createUserRequest.getUsername())
                .build();

        return userRepository.save(user);
    }


    @KafkaListener(topics = "wallet-created-topic", groupId = "wallet")
    public void assignWalletIdToRespectiveUser(String walletCreatedResponseMessage) {
        try {
            WalletCreatedResponse walletCreatedResponse = objectMapper.readValue(walletCreatedResponseMessage,
                    WalletCreatedResponse.class);

            Optional<User> optional = findById(UUID.fromString(walletCreatedResponse.getUserId()));

            if (optional.isEmpty()) {
                throw new RuntimeException("There is not user with the given id: " + walletCreatedResponse.getUserId());
            }

            User user = optional.get();

            if(user.getWalletId() != null) {
                throw new RuntimeException("Given user has already a wallet.");
            }

            user.setWalletId(UUID.fromString(walletCreatedResponse.getWalletId()));

            userRepository.save(user);
        } catch (JsonMappingException e) {
            log.error("Error in mapping the JSON message: " + e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Error in processing the JSON message: " + e);
            throw new RuntimeException(e);
        }
    }

    public User updateUser(UpdateUserRequest updateUserRequest) {
        Optional<User> optional = userRepository.findById(updateUserRequest.getUserId());

        if(optional.isEmpty()) {
            return null;
        }

        User userUpdated = User.builder()
                .userId(optional.get().getUserId())
                .firstName(updateUserRequest.getFirstName())
                .lastName(updateUserRequest.getLastName())
                .username(updateUserRequest.getUsername())
                .socialSecurityNumber(updateUserRequest.getSocialSecurityNumber())
                .createdAt(optional.get().getCreatedAt())
                .walletId(optional.get().getWalletId())
                .build();

        return userRepository.save(userUpdated);
    }

    public void deleteById(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("User with given id does not exist!");
        }
    }
    @KafkaListener(topics = "wallet-deleted-topic", groupId = "wallet")
    public void deleteWalletFromUser(String message) {
        try {
            UUID walletId = objectMapper.readValue(message, UUID.class);

            User user = userRepository.findByWalletId(walletId);

            user.setWalletId(null);

            userRepository.save(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could process the wallet id.");
        }

    }
}
