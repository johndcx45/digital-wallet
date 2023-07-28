package com.challenge.wallet.controller;

import com.challenge.wallet.dto.transaction.BillPaymentTransactionRequest;
import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.dto.transaction.WithdrawTransactionRequest;
import com.challenge.wallet.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity deposit(@RequestBody DepositTransactionRequest transactionRequest) {
        transactionService.deposit(transactionRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity transfer(@RequestBody @Valid TransferTransactionRequest transactionRequest) {
        transactionService.transfer(transactionRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/withdraw")
    public ResponseEntity withdraw(@RequestBody WithdrawTransactionRequest withdrawTransactionRequest) {
        transactionService.withdraw(withdrawTransactionRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/bill")
    public ResponseEntity payBill(@RequestBody BillPaymentTransactionRequest billPaymentTransactionRequest) {
        transactionService.payBill(billPaymentTransactionRequest);

        return new ResponseEntity(HttpStatus.OK);
    }
}
