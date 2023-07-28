package com.challenge.wallet.dto.wallet;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletCreateRequest {
    private String userId;
}
