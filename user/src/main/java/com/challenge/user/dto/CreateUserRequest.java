package com.challenge.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserRequest implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;
}
