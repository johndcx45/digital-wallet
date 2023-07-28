package com.challenge.wallet.controller;

import com.challenge.wallet.dto.WalletCreateRequest;
import com.challenge.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid WalletCreateRequest walletCreateRequest) {
        walletService.createWallet(walletCreateRequest);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
