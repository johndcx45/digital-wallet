package com.challenge.wallet.domain.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("TRANSFER")
public class TransferTransaction extends Transaction {
    private UUID sourceWalletId;
    private UUID targetWalletId;
    private Double amount;

    @Override
    protected TransactionType transactionType() {
        return TransactionType.TRANSFER;
    }
}
