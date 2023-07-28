package com.challenge.wallet.service;

import com.challenge.wallet.domain.transaction.Transaction;
import com.challenge.wallet.domain.transaction.TransactionType;
import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.DepositTransactionRequest;
import com.challenge.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {
    private UserService userService;
    private WalletService walletService;

    private WalletRepository walletRepository;

    public TransactionService(UserService userService, WalletService walletService,
                              WalletRepository walletRepository) {
        this.userService = userService;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    public void deposit(DepositTransactionRequest depositTransactionRequest) {
        if(!userService.isUserValid(depositTransactionRequest.getUserId())) {
            throw new RuntimeException("Could not process deposit request! User is not valid.");
        }

        Wallet wallet = findWalletById(depositTransactionRequest.getWalletId());
        Transaction depositTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.DEPOSIT)
                .wallet(wallet)
                .amount(depositTransactionRequest.getAmount())
                .timestamp(new Date())
                .build();

        wallet.setBalance(wallet.getBalance() + depositTransactionRequest.getAmount());
        wallet.getTransactions().add(depositTransaction);

        walletRepository.save(wallet);
    }

    private Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId).orElseThrow(() ->
                new RuntimeException("Could not find the wallet with given id: " + walletId));
    }
}
