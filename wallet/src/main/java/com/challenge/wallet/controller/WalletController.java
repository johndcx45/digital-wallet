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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Wallet> create(@RequestBody @Valid WalletCreateRequest walletCreateRequest) {
        Wallet wallet = walletService.createWallet(walletCreateRequest);

        if(wallet == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
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

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getById(@PathVariable("id") UUID id) {
        Optional<Wallet> wallet = walletService.findById(id);

        if(wallet.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(wallet.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        walletService.deleteById(id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
