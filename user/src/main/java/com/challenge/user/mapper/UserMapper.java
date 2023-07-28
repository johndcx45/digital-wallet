package com.challenge.user.mapper;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;
import com.challenge.user.dto.WalletCreatedResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUserRequestToUser(CreateUserRequest createUserRequest);
}
