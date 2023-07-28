package com.challenge.wallet.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DepositTransactionRequest {
    private Double amount;
    private UUID walletId;
    private UUID userId;
}
