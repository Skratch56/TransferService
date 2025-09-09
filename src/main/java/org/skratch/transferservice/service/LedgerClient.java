package org.skratch.transferservice.service;

import lombok.extern.slf4j.Slf4j;
import org.skratch.transferservice.dto.LedgerTransferRequest;
import org.skratch.transferservice.dto.LedgerTransferResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class LedgerClient {

    private final RestTemplate restTemplate;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile long lastFailureTime = 0;
    private static final int FAILURE_THRESHOLD = 3;
    private static final long OPEN_STATE_DURATION_MS = 10_000; // 10s
    private static final String LEDGER_SERVICE_URL = "http://ledger-service:8081/ledger/transfer";

    public LedgerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LedgerTransferResponse sendTransfer(LedgerTransferRequest request) {
        if (isCircuitOpen()) {
            log.warn("Circuit OPEN - skipping call to LedgerService for transferId={}", request.getTransferId());
            return LedgerTransferResponse.builder()
                    .transferId(request.getTransferId())
                    .success(false)
                    .message("Circuit open - Ledger Service temporarily unavailable")
                    .build();
        }

        try {
            LedgerTransferResponse resp = restTemplate.postForObject(
                    LEDGER_SERVICE_URL, request, LedgerTransferResponse.class);
            resetFailures();
            return resp;
        } catch (Exception ex) {
            recordFailure();
            log.error("LedgerService call failed for transferId={}, error={}", request.getTransferId(), ex.getMessage());
            return LedgerTransferResponse.builder()
                    .transferId(request.getTransferId())
                    .success(false)
                    .message("Ledger call failed: " + ex.getMessage())
                    .build();
        }
    }

    private boolean isCircuitOpen() {
        if (failureCount.get() >= FAILURE_THRESHOLD) {
            if (System.currentTimeMillis() - lastFailureTime < OPEN_STATE_DURATION_MS) {
                return true;
            }
            resetFailures(); // half-open: let next call try again
        }
        return false;
    }

    private void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
    }

    private void resetFailures() {
        failureCount.set(0);
    }
}
