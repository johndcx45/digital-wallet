package com.challenge.wallet.service.impl;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;
import com.challenge.wallet.dto.wallet.WalletCreatedResponse;
import com.challenge.wallet.repository.WalletRepository;
import com.challenge.wallet.service.WalletService;
import com.challenge.wallet.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    private WalletRepository walletRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper objectMapper;

    public WalletServiceImpl(WalletRepository walletRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.walletRepository = walletRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public Wallet createWallet(WalletCreateRequest walletCreateRequest) {
        if(walletRepository.findByUserId(UUID.fromString(walletCreateRequest.getUserId())).isPresent()) {
            log.error("A wallet already exists for given user: {}", walletCreateRequest.getUserId());
            throw new RuntimeException("Could not create a wallet.");
        }

        Wallet newWallet  = Wallet.builder()
                .userId(UUID.fromString(walletCreateRequest.getUserId()))
                .balance(0.0)
                .transactions(new ArrayList<>())
                .build();

        Wallet wallet = walletRepository.save(newWallet);

        WalletCreatedResponse walletCreatedResponse = WalletCreatedResponse.builder()
                .walletId(wallet.getWalletId().toString())
                .userId(wallet.getUserId().toString())
                .build();

        try {
            kafkaTemplate.send(Constants.WALLET_CREATED_TOPIC, objectMapper.writeValueAsString(walletCreatedResponse));

            return wallet;
        } catch (JsonProcessingException e) {
            log.error("Error in sending the message for message broker.");
            throw new RuntimeException(e);
        }
    }

    public List<Wallet> findWithPagination(Integer page, Integer size) {
        if(page < 0 || size <= 0) {
            throw new RuntimeException("One of the parameters, page or size, is invalid.");
        }
        Page<Wallet> wallets = walletRepository.findAll(PageRequest.of(page, size));

        if(wallets.getTotalElements() == 0) {
            return null;
        }

        return wallets.getContent();
    }

    public void deleteById(UUID id) {
        try {
            walletRepository.deleteById(id);

            kafkaTemplate.send(Constants.WALLET_DELETED_TOPIC, objectMapper.writeValueAsString(id));
        } catch(Exception e) {
            throw new RuntimeException("Wallet with given id does not exist.");
        }
    }

    public Optional<Wallet> findById(UUID id) {
        return walletRepository.findById(id);
    }
}

