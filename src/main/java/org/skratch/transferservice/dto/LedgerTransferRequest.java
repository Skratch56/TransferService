package org.skratch.transferservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LedgerTransferRequest {
    private String transferId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
