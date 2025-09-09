package org.skratch.transferservice.service;

import org.skratch.transferservice.dto.BatchTransferRequest;
import org.skratch.transferservice.dto.BatchTransferResponse;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;

import java.util.UUID;

public interface TransferService {
    TransferResponse initiateTransfer(TransferRequest request, String idempotencyKey);
    TransferResponse getTransferById(UUID id);
    BatchTransferResponse initiateBatchTransfer(BatchTransferRequest request, String idempotencyKey);
}
