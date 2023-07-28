package com.challenge.wallet.service;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;
import com.challenge.wallet.dto.wallet.WalletCreatedResponse;
import com.challenge.wallet.mapper.WalletMapper;
import com.challenge.wallet.repository.WalletRepository;
import com.challenge.wallet.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {
    private WalletRepository walletRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private WalletMapper walletMapper;
    private ObjectMapper objectMapper;

    public WalletService(WalletRepository walletRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.walletRepository = walletRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.walletMapper = Mappers.getMapper(WalletMapper.class);
        this.objectMapper = new ObjectMapper();
    }

    public void createWallet(WalletCreateRequest walletCreateRequest) {
        try {
            if(walletRepository.findByUserId(UUID.fromString(walletCreateRequest.getUserId())).isPresent()) {
                log.error("A wallet already exists for given user: {}", walletCreateRequest.getUserId());
                throw new RuntimeException("Could not create a wallet.");
            }

            Wallet wallet  = Wallet.builder()
                    .userId(UUID.fromString(walletCreateRequest.getUserId()))
                    .balance(0.0)
                    .transactions(new ArrayList<>())
                    .build();

            walletRepository.save(wallet);

            WalletCreatedResponse walletCreatedResponse = WalletCreatedResponse.builder()
                    .walletId(wallet.getWalletId().toString())
                    .userId(wallet.getUserId().toString())
                    .build();

            kafkaTemplate.send(Constants.WALLET_CREATED_TOPIC, objectMapper.writeValueAsString(walletCreatedResponse));
        } catch (Exception e) {
            log.error("Error in processing the response. {}", e);
        }
    }

    public List<Wallet> findWithPagination(Integer page, Integer size) {
        Page<Wallet> wallets = walletRepository.findAll(PageRequest.of(page, size));

        if(wallets.getTotalElements() == 0) {
            return null;
        }

        return wallets.getContent();
    }
}
