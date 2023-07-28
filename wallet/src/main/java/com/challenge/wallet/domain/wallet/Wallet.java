package com.challenge.wallet.domain.wallet;

import com.challenge.wallet.domain.transaction.Transaction;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallet_tb")
public class Wallet {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "wallet_id")
    private UUID walletId;
    @Column(name = "user_id")
    private UUID userId;
    @OneToMany(targetEntity = Transaction.class, mappedBy = "walletId", fetch = FetchType.EAGER)
    private List<Transaction> transactions;
    private Double balance;
}
