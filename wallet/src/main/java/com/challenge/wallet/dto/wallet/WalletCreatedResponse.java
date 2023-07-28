package com.challenge.wallet.dto.wallet;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletCreatedResponse {
    private String userId;
    private String walletId;
}
