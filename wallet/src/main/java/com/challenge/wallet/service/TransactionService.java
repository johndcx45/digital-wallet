package com.challenge.wallet.service;

import com.challenge.wallet.domain.transaction.Transaction;
import com.challenge.wallet.domain.transaction.TransactionType;
import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.repository.TransactionRepository;
import com.challenge.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class TransactionService {
    private UserService userService;
    private WalletService walletService;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    public TransactionService(UserService userService, WalletService walletService,
                              WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void deposit(DepositTransactionRequest depositTransactionRequest) {
        log.info("Received an deposit request with given amount: {}", depositTransactionRequest.getAmount());
        if(!userService.isUserValid(depositTransactionRequest.getUserId())) {
            throw new RuntimeException("Could not process deposit request! User is not valid.");
        }

        Optional<Wallet> optional = walletRepository.findById(depositTransactionRequest.getWalletId());

        if(optional.isEmpty()) {
            throw new RuntimeException("Could not find wallet with given id: " +
                    depositTransactionRequest.getWalletId());
        }

        Wallet wallet = optional.get();

        Transaction depositTransaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .walletId(wallet.getWalletId())
                .amount(depositTransactionRequest.getAmount())
                .timestamp(new Date())
                .build();

        transactionRepository.save(depositTransaction);

        Double newBalance = wallet.getBalance() + depositTransactionRequest.getAmount();

        wallet.setBalance(newBalance);
        wallet.getTransactions().add(depositTransaction);

        walletRepository.save(wallet);
    }

    public void transfer(TransferTransactionRequest transactionRequest) {
        Optional<Wallet> optionalSourceWallet = walletRepository.findById(transactionRequest.getSourceWallet());
        Optional<Wallet> optionalTargetWallet = walletRepository.findById(transactionRequest.getTargetWallet());

        if(optionalSourceWallet.isEmpty() || optionalTargetWallet.isEmpty()) {
            throw new RuntimeException("At least one wallet is not valid.");
        }

        Wallet sourceWallet = optionalSourceWallet.get();
        Wallet targetWallet = optionalTargetWallet.get();

        if(sourceWallet.getBalance() < transactionRequest.getAmount()) {
            throw new RuntimeException("Source user does not have balance to perform this operation");
        }

        sourceWallet.setBalance(sourceWallet.getBalance() - transactionRequest.getAmount());
        targetWallet.setBalance(targetWallet.getBalance() + transactionRequest.getAmount());

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);
    }
}
