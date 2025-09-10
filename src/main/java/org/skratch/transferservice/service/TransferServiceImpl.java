package org.skratch.transferservice.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.skratch.transferservice.dto.BatchTransferRequest;
import org.skratch.transferservice.dto.BatchTransferResponse;
import org.skratch.transferservice.dto.LedgerTransferRequest;
import org.skratch.transferservice.dto.LedgerTransferResponse;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.exceptions.CircuitBreakerOpenException;
import org.skratch.transferservice.exceptions.ResourceNotFoundException;
import org.skratch.transferservice.exceptions.TransferException;
import org.skratch.transferservice.mapper.TransferMapper;
import org.skratch.transferservice.model.Transfer;
import org.skratch.transferservice.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final LedgerClient ledgerClient;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private static final String LEDGER_SERVICE_URL = "http://localhost:8081/ledger/transfer";

    public TransferServiceImpl(TransferRepository transferRepository, TransferMapper transferMapper, LedgerClient ledgerClient) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
        this.ledgerClient = ledgerClient;
    }
    @Override
    @Transactional
    public TransferResponse initiateTransfer(TransferRequest request, String idempotencyKey) {
        log.info("Starting transfer: from={} to={} amount={} idempotencyKey={}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount(), idempotencyKey);


        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new TransferException("Source and destination accounts must be different");
        }
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new TransferException("Transfer amount must be greater than zero");
        }

        return transferRepository.findByIdempotencyKey(idempotencyKey)
                .map(existing -> {
                    log.warn("Duplicate transfer request detected for idempotencyKey={}, returning existing result", idempotencyKey);
                    return transferMapper.toTransferResponse(existing);
                }).orElseGet(() -> {

                    Transfer transfer = transferMapper.toEntity(request, idempotencyKey);
                    transfer.setTransferId(UUID.randomUUID().toString());
                    Transfer saved = transferRepository.save(transfer);

                    log.debug("Saved transfer with transferId={} and status={}", saved.getTransferId(), saved.getStatus());

                    LedgerTransferRequest ledgerReq = new LedgerTransferRequest();
                    ledgerReq.setTransferId(saved.getTransferId());
                    ledgerReq.setFromAccountId(saved.getFromAccountId());
                    ledgerReq.setToAccountId(saved.getToAccountId());
                    ledgerReq.setAmount(saved.getAmount());

                    log.info("Sending transferId={} to Ledger Service", saved.getTransferId());

                    try {
                        LedgerTransferResponse ledgerResp = ledgerClient.sendTransfer(ledgerReq);

                        if (!ledgerResp.isSuccess()) {
                            throw new TransferException("Ledger transfer failed: " + ledgerResp.getMessage());
                        }

                        saved.setStatus(Transfer.Status.SUCCESS);
                        transferRepository.save(saved);

                        log.info("TransferId={} completed with status={}", saved.getTransferId(), saved.getStatus());
                        return transferMapper.toTransferResponse(saved);

                    } catch (CircuitBreakerOpenException ex) {
                        log.error("Circuit breaker open for transferId={}, marking FAILED. Reason={}",
                                saved.getTransferId(), ex.getMessage());

                        saved.setStatus(Transfer.Status.FAILED);
                        transferRepository.save(saved);

                        throw new TransferException("Transfer failed due to circuit breaker: " + ex.getMessage());
                    }
                });
    }

    @Override
    @Transactional
    public TransferResponse getTransferById(UUID id) {
        log.debug("Fetching transfer by transferId={}", id);
        return transferRepository.findByTransferId(id.toString())
                .map(transferMapper::toTransferResponse)
                .orElseThrow(() -> {
                    log.error("Transfer not found for transferId={}", id);
                    return new IllegalArgumentException("Transfer not found: " + id);
                });
    }

    @Override
    @Transactional
    public BatchTransferResponse initiateBatchTransfer(BatchTransferRequest request, String idempotencyKey) {
        log.info("Initiating batch transfer with {} transfers, idempotencyKey={}",
                request.getTransfers().size(), idempotencyKey);

        List<CompletableFuture<TransferResponse>> futures =
                IntStream.range(0, request.getTransfers().size())
                        .mapToObj(index -> {
                            TransferRequest dto = request.getTransfers().get(index);
                            String childKey = idempotencyKey + "-" + index;

                            return CompletableFuture.supplyAsync(
                                    () -> initiateTransfer(dto, childKey), executor);
                        })
                        .toList();

        List<TransferResponse> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        log.info("Batch transfer completed: {} transfers processed", results.size());

        return BatchTransferResponse.builder()
                .results(results)
                .build();
    }
}
