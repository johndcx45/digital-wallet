package com.challenge.wallet.service;

import com.challenge.wallet.domain.transaction.Transaction;
import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.transaction.BillPaymentTransactionRequest;
import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.dto.transaction.WithdrawTransactionRequest;
import com.challenge.wallet.repository.TransactionRepository;
import com.challenge.wallet.repository.WalletRepository;
import com.challenge.wallet.service.impl.TransactionServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private TransactionService transactionService;
    @Mock
    private UserService userService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    private UUID walletId;
    private UUID targetWalletId;
    private UUID userId;
    private Wallet wallet;
    private Wallet targetWallet;
    private DepositTransactionRequest depositTransactionRequest;
    private TransferTransactionRequest transferTransactionRequest;
    private WithdrawTransactionRequest withdrawTransactionRequest;
    private BillPaymentTransactionRequest billPaymentTransactionRequest;

    @BeforeEach
    void setup() {
        Double amount = 1000.0;
        walletId = UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f");
        targetWalletId = UUID.fromString("c46fe5eb-0ea6-4a83-825f-fc7451cce2a8");
        userId = UUID.fromString("80648b2f-394c-42db-b554-9324a9db9cf7");
        transactionService = new TransactionServiceImpl(userService, walletRepository,
                transactionRepository);
        depositTransactionRequest = new DepositTransactionRequest(
                amount,
                walletId,
                userId
        );
        wallet = Wallet.builder()
                .walletId(walletId)
                .userId(userId)
                .balance(1000.0)
                .transactions(new ArrayList<>())
                .build();
        targetWallet = Wallet.builder()
                .walletId(targetWalletId)
                .userId(userId)
                .balance(700.0)
                .transactions(new ArrayList<>())
                .build();
        transferTransactionRequest = new TransferTransactionRequest(
                walletId,
                targetWalletId,
                amount
        );
        withdrawTransactionRequest = new WithdrawTransactionRequest(
            walletId,
            amount
        );
        billPaymentTransactionRequest = new BillPaymentTransactionRequest(
                walletId,
                amount
        );
    }

    @Test
    void givenValidDepositTransactionRequest_whenDeposit_thenDepositSuccessfully() {
        when(userService.isUserValid(Mockito.any(UUID.class))).thenReturn(true);
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));

        transactionService.deposit(depositTransactionRequest);

        verify(transactionRepository,times(1)).save(Mockito.any(Transaction.class));
        verify(walletRepository, times(1)).save(Mockito.any(Wallet.class));
    }

    @Test
    void givenInvalidAmount_whenDeposit_thenThrowRuntimeException() {
        depositTransactionRequest.setAmount(0.0);
        Assertions.assertThatThrownBy(() -> {
            transactionService.deposit(depositTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenInvalidUser_whenDeposit_shouldThrowException() {
        when(userService.isUserValid(Mockito.any(UUID.class))).thenReturn(false);

        Assertions.assertThatThrownBy(() -> {
            transactionService.deposit(depositTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidTransferTransactionRequest_whenTransfer_thenTransferSuccessfully() {
        when(walletRepository.findById(UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f")))
                .thenReturn(Optional.of(wallet));
        when(walletRepository.findById(UUID.fromString("c46fe5eb-0ea6-4a83-825f-fc7451cce2a8")))
                .thenReturn(Optional.of(targetWallet));

        transactionService.transfer(transferTransactionRequest);

        verify(transactionRepository, times(1)).save(Mockito.any(Transaction.class));
        verify(walletRepository, times(2)).save(Mockito.any(Wallet.class));
    }

    @Test
    void givenInvalidSourceWallet_whenTransfer_thenThrowException() {
        when(walletRepository.findById(UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f")))
                .thenReturn(Optional.empty());
        when(walletRepository.findById(UUID.fromString("c46fe5eb-0ea6-4a83-825f-fc7451cce2a8")))
                .thenReturn(Optional.of(targetWallet));


        Assertions.assertThatThrownBy(() -> {
            transactionService.transfer(transferTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenInvalidTargetWallet_whenTransfer_thenThrowException() {
        when(walletRepository.findById(UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f")))
                .thenReturn(Optional.of(wallet));
        when(walletRepository.findById(UUID.fromString("c46fe5eb-0ea6-4a83-825f-fc7451cce2a8")))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            transactionService.transfer(transferTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenInvalidAmount_whenTransfer_thenThrowException() {
        transferTransactionRequest.setAmount(0.0);

        Assertions.assertThatThrownBy(() -> {
            transactionService.transfer(transferTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidWithdrawTransactionRequest_whenWithdraw_thenWithdrawSuccessfully() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));

        transactionService.withdraw(withdrawTransactionRequest);

        verify(transactionRepository, times(1)).save(Mockito.any(Transaction.class));
        verify(walletRepository, times(1)).save(Mockito.any(Wallet.class));
    }

    @Test
    void giveInvalidWalletId_whenWithdraw_thenThrowException() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            transactionService.withdraw(withdrawTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void giveInvalidAmount_whenWithdraw_thenThrowException() {
        withdrawTransactionRequest.setAmount(0.0);

        Assertions.assertThatThrownBy(() -> {
            transactionService.withdraw(withdrawTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void giveAmountGreaterThanBalance_whenWithdraw_thenThrowException() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));
        withdrawTransactionRequest.setAmount(10000.0);

        Assertions.assertThatThrownBy(() -> {
            transactionService.withdraw(withdrawTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenBillPaymentTransactionRequest_whenPayBill_thenPayBillSuccessfully() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));

        transactionService.payBill(billPaymentTransactionRequest);

        verify(transactionRepository, times(1)).save(Mockito.any(Transaction.class));
        verify(walletRepository, times(1)).save(Mockito.any(Wallet.class));
    }

    @Test
    void givenInvalidAmount_whenPayBill_thenThrowException() {
        billPaymentTransactionRequest.setAmount(0.0);

        Assertions.assertThatThrownBy(() -> {
            transactionService.payBill(billPaymentTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenWalletId_whenPayBill_thenThrowException() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            transactionService.payBill(billPaymentTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenAmountGreaterThanBalance_whenPayBill_thenThrowException() {
        billPaymentTransactionRequest.setAmount(10000.0);

        Assertions.assertThatThrownBy(() -> {
            transactionService.payBill(billPaymentTransactionRequest);
        }).isInstanceOf(RuntimeException.class);
    }
}