package org.skratch.transferservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class LedgerTransferResponse {
    private String transferId;
    private boolean success;
    private String message;
}
