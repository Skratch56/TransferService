package org.skratch.transferservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.skratch.transferservice.dto.LedgerTransferRequest;
import org.skratch.transferservice.dto.LedgerTransferResponse;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.exceptions.TransferException;
import org.skratch.transferservice.mapper.TransferMapper;
import org.skratch.transferservice.model.Transfer;
import org.skratch.transferservice.repository.TransferRepository;
import org.skratch.transferservice.service.LedgerClient;
import org.skratch.transferservice.service.TransferService;
import org.skratch.transferservice.service.TransferServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferServiceApplicationTests {

    @Mock
    TransferRepository transferRepository;
    @Mock TransferMapper transferMapper;
    @Mock LedgerClient ledgerClient;

    @InjectMocks
    TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        transferRepository = mock(TransferRepository.class);
        transferMapper = mock(TransferMapper.class);
        ledgerClient = mock(LedgerClient.class);

        transferService = new TransferServiceImpl(transferRepository, transferMapper, ledgerClient);
    }

    @Test
    void shouldRespectIdempotency() {
        String idempotencyKey = "idem-123";
        Transfer existing = new Transfer();
        existing.setTransferId(UUID.randomUUID().toString());
        existing.setStatus(Transfer.Status.SUCCESS);

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existing));
        when(transferMapper.toTransferResponse(existing)).thenReturn(new TransferResponse());

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.TEN);

        TransferResponse response = transferService.initiateTransfer(req, idempotencyKey);

        assertThat(response).isNotNull();
        verify(transferRepository, never()).save(any());
        verify(ledgerClient, never()).sendTransfer(any());
    }


    @Test
    void shouldMarkTransferSuccessWhenLedgerSucceeds() {
        String idempotencyKey = "idem-124";
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.valueOf(100));

        Transfer newTransfer = new Transfer();
        newTransfer.setId(1L);
        newTransfer.setFromAccountId(1L);
        newTransfer.setToAccountId(2L);
        newTransfer.setAmount(BigDecimal.valueOf(100));
        newTransfer.setStatus(Transfer.Status.PENDING);

        Transfer saved = new Transfer();
        saved.setTransferId(UUID.randomUUID().toString());
        saved.setFromAccountId(1L);
        saved.setToAccountId(2L);
        saved.setAmount(BigDecimal.valueOf(100));
        saved.setStatus(Transfer.Status.PENDING);

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(transferMapper.toEntity(req, idempotencyKey)).thenReturn(newTransfer);
        when(transferRepository.save(newTransfer)).thenReturn(saved);
        when(ledgerClient.sendTransfer(any(LedgerTransferRequest.class)))
                .thenReturn(LedgerTransferResponse.builder()
                        .success(true)
                        .message("OK")
                        .build());
        when(transferMapper.toTransferResponse(any(Transfer.class))).thenReturn(new TransferResponse());

        TransferResponse resp = transferService.initiateTransfer(req, idempotencyKey);

        assertThat(resp).isNotNull();
        verify(transferRepository, times(2)).save(any(Transfer.class)); // initial + after update
    }

    @Test
    void shouldThrowExceptionWhenLedgerFails() {
        String idempotencyKey = "idem-125";
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.valueOf(100));

        Transfer saved = new Transfer();
        saved.setTransferId(UUID.randomUUID().toString());
        saved.setStatus(Transfer.Status.PENDING);

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(transferMapper.toEntity(req, idempotencyKey)).thenReturn(saved);
        when(transferRepository.save(saved)).thenReturn(saved);
        when(ledgerClient.sendTransfer(any(LedgerTransferRequest.class)))
                .thenReturn(LedgerTransferResponse.builder()
                        .success(false)
                        .message("Failed")
                        .build());

        assertThatThrownBy(() -> transferService.initiateTransfer(req, idempotencyKey))
                .isInstanceOf(TransferException.class)
                .hasMessageContaining("Ledger transfer failed: Failed");
    }

    @Test
    void shouldThrowWhenTransferNotFound() {
        UUID id = UUID.randomUUID();
        when(transferRepository.findByTransferId(id.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.getTransferById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transfer not found");
    }

}
