package com.challenge.user.service;

import com.challenge.user.domain.User;
import com.challenge.user.dto.UserCreateRequest;
import com.challenge.user.mapper.UserMapper;
import com.challenge.user.repository.UserRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;

    private UserMapper userMapper;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper = Mappers.getMapper(UserMapper.class);
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

    public void createUser(UserCreateRequest userCreateRequest) {
        User user = userMapper.userCreateRequestToUser(userCreateRequest);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public void deleteById(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("User with given id does not exist!");
        }
    }
}
