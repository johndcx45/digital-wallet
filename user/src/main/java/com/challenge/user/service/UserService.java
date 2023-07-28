package com.challenge.user.service;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;
import com.challenge.user.dto.CreateWalletRequest;
import com.challenge.user.dto.WalletCreatedResponse;
import com.challenge.user.mapper.UserMapper;
import com.challenge.user.repository.UserRepository;
import com.challenge.user.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.objectMapper = new ObjectMapper();
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<User> findWithPagination(Integer page, Integer size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));

        if(users.getTotalElements() == 0) {
            return null;
        }

        return users.getContent();
    }

    public User findById(UUID id) {
        Optional<User> optional = userRepository.findById(id);

        if(optional.isEmpty()) {
            return null;
        }

        return optional.get();
    }

    public void createUser(CreateUserRequest createUserRequest) {
        User userMapped = userMapper.createUserRequestToUser(createUserRequest);

        User user = User.builder()
                .createdAt(new Date())
                .userId(userMapped.getUserId())
                .firstName(userMapped.getFirstName())
                .lastName(userMapped.getLastName())
                .socialSecurityNumber(userMapped.getSocialSecurityNumber())
                .username(userMapped.getUsername())
                .build();

        userRepository.save(user);
    }


    @KafkaListener(topics = "${WALLET_CREATED_TOPIC}", groupId = "wallet")
    public void assignWalletIdToRespectiveUser(String walletCreatedResponseMessage) {
        try {
            WalletCreatedResponse walletCreatedResponse = objectMapper.readValue(walletCreatedResponseMessage,
                    WalletCreatedResponse.class);

            User user = findById(UUID.fromString(walletCreatedResponse.getUserId()));

            if (user == null) {
                throw new RuntimeException("There is not user with the given id: {}" + walletCreatedResponse.getUserId());
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

    public void deleteById(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("User with given id does not exist!");
        }
    }
}
