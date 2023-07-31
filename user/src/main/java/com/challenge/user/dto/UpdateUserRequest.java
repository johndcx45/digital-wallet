package com.challenge.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserRequest implements Serializable {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;
}
