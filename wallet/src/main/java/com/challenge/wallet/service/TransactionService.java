package com.challenge.wallet.service;

import com.challenge.wallet.domain.transaction.Transaction;
import com.challenge.wallet.domain.transaction.TransactionType;
import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.transaction.BillPaymentTransactionRequest;
import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.dto.transaction.WithdrawTransactionRequest;
import com.challenge.wallet.repository.TransactionRepository;
import com.challenge.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class TransactionService {
    private UserService userService;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    public TransactionService(UserService userService, WalletRepository walletRepository,
                              TransactionRepository transactionRepository) {
        this.userService = userService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void deposit(DepositTransactionRequest depositTransactionRequest) {
        if(depositTransactionRequest.getAmount() <= 0) {
            throw new RuntimeException("Amount is invalid.");
        }

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

        Double newBalance = wallet.getBalance() + depositTransactionRequest.getAmount();

        wallet.setBalance(newBalance);
        wallet.getTransactions().add(depositTransaction);

        transactionRepository.save(depositTransaction);
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

        if(transactionRequest.getAmount() <= 0) {
            throw new RuntimeException("Amount is invalid.");
        }

        sourceWallet.setBalance(sourceWallet.getBalance() - transactionRequest.getAmount());
        targetWallet.setBalance(targetWallet.getBalance() + transactionRequest.getAmount());

        Transaction transaction = Transaction.builder()
                        .transactionType(TransactionType.TRANSFER)
                        .timestamp(new Date())
                        .amount(transactionRequest.getAmount())
                        .walletId(transactionRequest.getSourceWallet())
                        .targetWallet(transactionRequest.getTargetWallet())
                        .build();

        sourceWallet.getTransactions().add(transaction);

        transactionRepository.save(transaction);
        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);
    }

    public void withdraw(WithdrawTransactionRequest withdrawTransactionRequest) {
        Optional<Wallet> optional = walletRepository.findById(withdrawTransactionRequest.getSourceWallet());

        if(optional.isEmpty()) {
            throw new RuntimeException("Could not find wallet for given id: " +
                    withdrawTransactionRequest.getSourceWallet());
        }

        if(withdrawTransactionRequest.getAmount() <= 0) {
            throw new RuntimeException("Amount is invalid.");
        }

        Wallet wallet = optional.get();

        if(wallet.getBalance() < withdrawTransactionRequest.getAmount()) {
            throw new RuntimeException("Source user does not have balance to perform this operation");
        }

        wallet.setBalance(wallet.getBalance() - withdrawTransactionRequest.getAmount());

        Transaction transaction = Transaction.builder()
                        .transactionType(TransactionType.WITHDRAW)
                        .walletId(wallet.getWalletId())
                        .timestamp(new Date())
                        .amount(withdrawTransactionRequest.getAmount())
                        .build();

        wallet.getTransactions().add(transaction);

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }

    public void payBill(BillPaymentTransactionRequest billPaymentTransactionRequest) {
        if(billPaymentTransactionRequest.getAmount() <= 0) {
            throw new RuntimeException("Bill value is not valid.");
        }

        Optional<Wallet> optional = walletRepository.findById(billPaymentTransactionRequest.getSourceWallet());

        if(optional.isEmpty()) {
            throw new RuntimeException("Could not find wallet for given id: " +
                    billPaymentTransactionRequest.getSourceWallet());
        }

        Wallet wallet = optional.get();

        if(wallet.getBalance() < billPaymentTransactionRequest.getAmount()) {
            throw new RuntimeException("Source user does not have balance to perform this operation");
        }

        wallet.setBalance(wallet.getBalance() - billPaymentTransactionRequest.getAmount());

        Transaction transaction = Transaction.builder()
                .amount(billPaymentTransactionRequest.getAmount())
                .transactionType(TransactionType.BILL_PAYMENT)
                .timestamp(new Date())
                .walletId(wallet.getWalletId())
                .build();

        wallet.getTransactions().add(transaction);

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }
}
