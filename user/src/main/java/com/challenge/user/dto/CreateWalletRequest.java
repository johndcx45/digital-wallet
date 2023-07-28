package com.challenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateWalletRequest {
    private String userId;
    private String username;
    private String firstName;
    private String socialSecurityNumber;
    private String lastName;
}
