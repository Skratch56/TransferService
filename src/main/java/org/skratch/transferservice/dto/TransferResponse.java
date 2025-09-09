package org.skratch.transferservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransferResponse {
    private String transferId;
    private String status;
    private String idempotencyKey;
    private LocalDateTime createdAt;
}
