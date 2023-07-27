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
@DiscriminatorValue("BILL_PAYMENT")
public class BillPaymentTransaction extends Transaction {
    private UUID walletId;
    private Double billValue;

    @Override
    protected TransactionType transactionType() {
        return TransactionType.BILL_PAYMENT;
    }
}
