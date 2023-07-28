package com.challenge.wallet.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID userId;
    private String username;
    private String firstName;
    private String socialSecurityNumber;
    private String lastName;
    private Date createdAt;
    private UUID walletId;
}
