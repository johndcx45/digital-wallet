package com.challenge.user.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tb")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID userId;
    @NotNull
    @Size(min = 2, max = 25)
    @Column(unique = true)
    private String username;
    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "first_name")
    private String firstName;
    @Size(min = 11, max = 11)
    private String socialSecurityNumber;
    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "last_name")
    private String lastName;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "wallet_id")
    private UUID walletId;
}
