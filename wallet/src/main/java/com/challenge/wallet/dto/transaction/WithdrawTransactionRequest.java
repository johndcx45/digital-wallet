package com.challenge.wallet.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class WithdrawTransactionRequest {
    private UUID sourceWallet;
    private Double amount;
}
