package com.challenge.wallet.service;

import com.challenge.wallet.domain.wallet.Wallet;
import com.challenge.wallet.dto.wallet.WalletCreateRequest;
import com.challenge.wallet.repository.WalletRepository;
import com.challenge.wallet.service.impl.WalletServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    private WalletService walletService;
    @Mock
    private WalletRepository walletRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private WalletCreateRequest walletCreateRequest;
    @Mock
    private Wallet wallet;
    private UUID walletId;
    private UUID userId;
    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        walletService = new WalletServiceImpl(walletRepository, kafkaTemplate);
        walletId = UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f");
        userId = UUID.fromString("80648b2f-394c-42db-b554-9324a9db9cf7");
        wallet = Wallet.builder()
                .walletId(walletId)
                .userId(userId)
                .balance(0.0)
                .transactions(new ArrayList<>())
                .build();
        walletCreateRequest = WalletCreateRequest.builder()
                .userId(userId.toString())
                .build();
    }

    @Test
    void givenValidWalletCreateRequest_whenCreateWallet_thenReturnWallet() {
        when(walletRepository.findByUserId(Mockito.any(UUID.class))).thenReturn(Optional.empty());
        when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(wallet);

        Wallet wallet = walletService.createWallet(walletCreateRequest);

        Assertions.assertThat(wallet.getWalletId()).isEqualTo(walletId);
    }

    @Test
    void givenInvalidWalletCreateRequest_whenCreateWallet_thenReturnWallet() {
        when(walletRepository.findByUserId(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));

        Assertions.assertThatThrownBy(() -> {
            walletService.createWallet(walletCreateRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidPageAndSize_whenFindWithPagination_thenReturnWallets() {
        Integer page = 0;
        Integer size = 1;

        Page<Wallet> walletPage = new PageImpl<>(Arrays.asList(wallet));

        when(walletRepository.findAll(PageRequest.of(page, size))).thenReturn(walletPage);

        List<Wallet> response = walletService.findWithPagination(page, size);

        verify(walletRepository, times(1)).findAll(PageRequest.of(page, size));
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    @Test
    void givenInvalidPageAndSize_whenFindWithPagination_thenReturnWallets() {
        Integer page = -1;
        Integer size = -1;

        Assertions.assertThatThrownBy(() -> {
           walletService.findWithPagination(page, size);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidWalletId_whenDeleteById_thenDelete() {
        walletService.deleteById(walletId);

        verify(walletRepository, times(1)).deleteById(walletId);
        verify(kafkaTemplate, times(1)).send(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void givenValidWalletId_whenFindById_thenReturnWallet() {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(wallet));

        Optional<Wallet> optional = walletService.findById(walletId);

        verify(walletRepository, times(1)).findById(walletId);
        Assertions.assertThat(optional.isPresent()).isEqualTo(true);
    }
}