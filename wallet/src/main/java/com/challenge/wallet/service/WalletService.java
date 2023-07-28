package com.challenge.wallet.service;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.WalletCreateRequest;
import com.challenge.wallet.dto.WalletCreatedResponse;
import com.challenge.wallet.mapper.WalletMapper;
import com.challenge.wallet.repository.WalletRepository;
import com.challenge.wallet.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            Wallet wallet = walletMapper.walletCreateRequestToWallet(walletCreateRequest);

            Wallet.builder()
                    .balance(0.0)
                    .walletId(UUID.randomUUID())
                    .transactions(new ArrayList<>())
                    .build();

            walletRepository.save(wallet);

            WalletCreatedResponse walletCreatedResponse = WalletCreatedResponse.builder()
                    .walletId(wallet.getWalletId().toString())
                    .userId(wallet.getUserId().toString())
                    .build();

            kafkaTemplate.send(Constants.WALLET_CREATED_TOPIC, objectMapper.writeValueAsString(walletCreatedResponse));
        } catch (Exception e) {
            log.error("Error in processing the response.");
        }
    }
}
