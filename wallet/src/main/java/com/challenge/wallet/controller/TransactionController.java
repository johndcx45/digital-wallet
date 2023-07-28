package com.challenge.wallet.controller;

import com.challenge.wallet.dto.transaction.DepositTransactionRequest;
import com.challenge.wallet.dto.transaction.TransferTransactionRequest;
import com.challenge.wallet.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
