package com.challenge.wallet.mapper;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet walletCreateRequestToWallet(WalletCreateRequest walletCreateRequest);
}
