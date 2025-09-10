package org.skratch.transferservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skratch.transferservice.dto.*;
import org.skratch.transferservice.exceptions.TransferException;
import org.skratch.transferservice.model.Transfer;
import org.skratch.transferservice.repository.TransferRepository;
import org.skratch.transferservice.service.LedgerClient;
import org.skratch.transferservice.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;
    @Autowired
    private TransferRepository transferRepository;

    @MockitoBean
    private LedgerClient ledgerClient;

    private TransferRequest request;

    @BeforeEach
    void setup() {
        request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    void happyPathTransfer() {
        Mockito.when(ledgerClient.sendTransfer(any(LedgerTransferRequest.class)))
                .thenReturn(LedgerTransferResponse.builder()
                        .success(true)
                        .message("OK")
                        .build());

        TransferResponse resp = transferService.initiateTransfer(request, "idem-happy");

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("SUCCESS");

        Optional<Transfer> saved = transferRepository.findByIdempotencyKey("idem-happy");
        assertThat(saved).isPresent();
        assertThat(saved.get().getStatus()).isEqualTo(Transfer.Status.SUCCESS);
    }

    @Test
    void idempotencyShouldReturnSameResponse() {
        Mockito.when(ledgerClient.sendTransfer(any(LedgerTransferRequest.class)))
                .thenReturn(LedgerTransferResponse.builder()
                        .success(true)
                        .message("OK")
                        .build());

        TransferResponse first = transferService.initiateTransfer(request, "idem-123");
        TransferResponse second = transferService.initiateTransfer(request, "idem-123");

        assertThat(second).isNotNull();
        assertThat(second.getStatus()).isEqualTo(first.getStatus());
        assertThat(transferRepository.findByIdempotencyKey("idem-123")).isPresent();
    }

    @Test
    void shouldMarkTransferFailedWhenLedgerFails() {
        Mockito.when(ledgerClient.sendTransfer(any(LedgerTransferRequest.class)))
                .thenReturn(LedgerTransferResponse.builder()
                        .success(false)
                        .message("Ledger down")
                        .build());

        assertThatThrownBy(() -> transferService.initiateTransfer(request, "idem-fail"))
                .isInstanceOf(TransferException.class)
                .hasMessageContaining("Ledger transfer failed: Ledger down");
    }

}
