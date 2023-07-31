package com.challenge.wallet.service;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletService {
    Wallet createWallet(WalletCreateRequest walletCreateRequest);
    List<Wallet> findWithPagination(Integer page, Integer size);
    void deleteById(UUID id);
    Optional<Wallet> findById(UUID id);
}
