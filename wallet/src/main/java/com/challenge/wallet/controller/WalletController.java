package com.challenge.wallet.controller;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;
import com.challenge.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Wallet>> get(@RequestParam("page") @Min(0) Integer page,
                                            @RequestParam("size") @Min(1) Integer size) {
        List<Wallet> wallets = walletService.findWithPagination(page, size);

        if(wallets == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }
}
