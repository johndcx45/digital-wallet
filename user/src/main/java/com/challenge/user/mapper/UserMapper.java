package com.challenge.user.mapper;

import com.challenge.user.domain.User;
import com.challenge.user.dto.UserCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userCreateRequestToUser(UserCreateRequest userCreateRequest);
}
