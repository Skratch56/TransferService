package org.skratch.transferservice.service;

import lombok.extern.slf4j.Slf4j;
import org.skratch.transferservice.dto.LedgerTransferRequest;
import org.skratch.transferservice.dto.LedgerTransferResponse;
import org.skratch.transferservice.exceptions.CircuitBreakerOpenException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ObjectInputFilter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class LedgerClient {
    private final Properties config;
    private final RestTemplate restTemplate;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile long lastFailureTime = 0;
    private static final int FAILURE_THRESHOLD = 3;
    private static final long OPEN_STATE_DURATION_MS = 10_000; // 10s


    public LedgerClient(RestTemplate restTemplate, Properties config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public LedgerTransferResponse sendTransfer(LedgerTransferRequest request) {
        String ledgerServiceUrl = config.getProperty("ledger.service.transfer");
        if (isCircuitOpen()) {
            String msg = "Circuit OPEN - Ledger Service temporarily unavailable for transferId=" + request.getTransferId();
            log.warn(msg);
            throw new CircuitBreakerOpenException(msg);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            headers.set("X-Request-ID", correlationId);
        }

        try {
            LedgerTransferResponse resp = restTemplate.postForObject(
                    ledgerServiceUrl, request, LedgerTransferResponse.class);
            resetFailures();
            return resp;
        } catch (Exception ex) {
            recordFailure();
            String msg = "LedgerService call failed for transferId=" + request.getTransferId() + ", error=" + ex.getMessage();
            log.error(msg);
            throw new CircuitBreakerOpenException("Ledger call failed: " + ex.getMessage());
        }
    }

    private boolean isCircuitOpen() {
        if (failureCount.get() >= FAILURE_THRESHOLD) {
            if (System.currentTimeMillis() - lastFailureTime < OPEN_STATE_DURATION_MS) {
                return true;
            }
            resetFailures();
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
