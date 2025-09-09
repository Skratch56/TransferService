package org.skratch.transferservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BatchTransferResponse {
    private List<TransferResponse> results;
}
