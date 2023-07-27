package com.challenge.wallet.domain.wallet;

import org.hibernate.annotations.GenericGenerator;

import javax.annotation.processing.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallet_tb")
public class Wallet {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private UUID userId;
}
