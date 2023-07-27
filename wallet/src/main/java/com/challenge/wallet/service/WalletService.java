package com.challenge.wallet.service;

import com.challenge.wallet.dto.DepositTransactionRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class WalletService {

    private UserService userService;

    public WalletService(UserService userService) {
        this.userService = userService;
    }

    public void deposit(DepositTransactionRequest transactionRequest) {
        if(userService.isUserValid()) {

        }
    }
}
