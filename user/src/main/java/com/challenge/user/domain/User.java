package com.challenge.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tb")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Column(unique = true)
    private String username;
    @Column(name = "first_name", unique = true)
    private String firstName;
    @Column(name = "last_name", unique = true)
    private String lastName;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "wallet_id")
    private UUID walletId;
}
