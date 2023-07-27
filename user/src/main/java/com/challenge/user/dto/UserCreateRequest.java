package com.challenge.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCreateRequest {
    private String username;
    private String firstName;
    private String lastName;
}
