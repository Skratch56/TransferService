package org.skratch.transferservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LedgerTransferResponse {
    private String transferId;
    private boolean success;
    private String message;
}
