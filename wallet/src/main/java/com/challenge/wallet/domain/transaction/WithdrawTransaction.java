package com.challenge.wallet.domain.transaction;

import com.challenge.wallet.domain.transaction.Transaction;
import com.challenge.wallet.domain.transaction.TransactionType;
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
@DiscriminatorValue("WITHDRAW")
public class WithdrawTransaction extends Transaction {
    private Double amount;
    private UUID walletId;

    @Override
    protected TransactionType transactionType() {
        return TransactionType.WITHDRAW;
    }
}
