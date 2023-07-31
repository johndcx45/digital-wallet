package com.challenge.wallet.service;

import com.challenge.wallet.dto.transaction.BillPaymentTransactionRequest;
import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.dto.transaction.WithdrawTransactionRequest;

public interface TransactionService {
    void deposit(DepositTransactionRequest depositTransactionRequest);
    void transfer(TransferTransactionRequest transferTransactionRequest);
    void withdraw(WithdrawTransactionRequest withdrawTransactionRequest);
    void payBill(BillPaymentTransactionRequest billPaymentTransactionRequest);
}