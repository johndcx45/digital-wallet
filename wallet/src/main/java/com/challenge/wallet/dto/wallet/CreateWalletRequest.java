package com.challenge.wallet.dto.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletRequest {
    private String userId;
    private String username;
    private String firstName;
    private String socialSecurityNumber;
    private String lastName;
}
