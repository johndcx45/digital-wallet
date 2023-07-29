package com.challenge.user.service;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    List<User> findWithPagination(Integer page, Integer size);
    Optional<User> findById(UUID id);
    User createUser(CreateUserRequest createUserRequest);
    void deleteById(UUID id);
    void assignWalletIdToRespectiveUser(String walletCreatedResponseMessage);
}
