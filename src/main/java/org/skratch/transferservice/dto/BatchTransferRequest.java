package org.skratch.transferservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchTransferRequest {
    private List<TransferRequest> transfers;
}
