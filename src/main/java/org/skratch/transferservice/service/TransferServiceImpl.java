package org.skratch.transferservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.skratch.transferservice.dto.BatchTransferRequest;
import org.skratch.transferservice.dto.BatchTransferResponse;
import org.skratch.transferservice.dto.LedgerTransferRequest;
import org.skratch.transferservice.dto.LedgerTransferResponse;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.mapper.BatchTransferMapper;
import org.skratch.transferservice.mapper.TransferMapper;
import org.skratch.transferservice.model.Transfer;
import org.skratch.transferservice.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final BatchTransferMapper batchTransferMapper;
    private final LedgerClient ledgerClient;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private static final String LEDGER_SERVICE_URL = "http://ledger-service:8081/ledger/transfer";

    @Override
    @Transactional
    public TransferResponse initiateTransfer(TransferRequest request, String idempotencyKey) {
        // Check idempotency
        return transferRepository.findByIdempotencyKey(idempotencyKey)
                .map(transferMapper::toTransferResponse)
                .orElseGet(() -> {
                    // create transfer
                    Transfer transfer = transferMapper.toEntity(request, idempotencyKey);
                    transfer.setTransferId(UUID.randomUUID().toString());
                    Transfer saved = transferRepository.save(transfer);

                    // build Ledger request
                    LedgerTransferRequest ledgerReq = new LedgerTransferRequest();
                    ledgerReq.setTransferId(saved.getTransferId());
                    ledgerReq.setFromAccountId(saved.getFromAccountId());
                    ledgerReq.setToAccountId(saved.getToAccountId());
                    ledgerReq.setAmount(saved.getAmount());

                    // call Ledger Service with circuit breaker
                    LedgerTransferResponse ledgerResp = ledgerClient.sendTransfer(ledgerReq);

                    // update transfer status
                    saved.setStatus(ledgerResp.isSuccess()
                            ? Transfer.Status.SUCCESS
                            : Transfer.Status.FAILED);
                    transferRepository.save(saved);

                    return transferMapper.toTransferResponse(saved);
                });
    }

    @Override
    @Transactional
    public TransferResponse getTransferById(UUID id) {
        return transferRepository.findByTransferId(id.toString())
                .map(transferMapper::toTransferResponse)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + id));
    }

    @Override
    @Transactional
    public BatchTransferResponse initiateBatchTransfer(BatchTransferRequest request, String idempotencyKey) {
        List<CompletableFuture<TransferResponse>> futures = request.getTransfers().stream()
                .map(dto -> CompletableFuture.supplyAsync(
                        () -> initiateTransfer(dto, idempotencyKey), executor))
                .toList();

        List<TransferResponse> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return BatchTransferResponse.builder().results(results).build();
    }
}
