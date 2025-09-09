package org.skratch.transferservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.skratch.transferservice.dto.BatchTransferRequest;
import org.skratch.transferservice.dto.BatchTransferResponse;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "Endpoints for initiating and managing transfers")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Create a transfer", description = "Initiates a new transfer between accounts. Requires Idempotency-Key header.")
    @ApiResponse(responseCode = "201", description = "Transfer created successfully")
    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransferRequest request) {
        TransferResponse response = transferService.initiateTransfer(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get transfer status", description = "Fetches the status of a transfer by transferId.")
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getTransfer(@PathVariable UUID id) {
        TransferResponse response = transferService.getTransferById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Batch transfers", description = "Processes up to 20 transfers concurrently.")
    @ApiResponse(responseCode = "201", description = "Batch processed successfully")
    @PostMapping("/batch")
    public ResponseEntity<BatchTransferResponse> createBatchTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid BatchTransferRequest request) {
        BatchTransferResponse response = transferService.initiateBatchTransfer(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
