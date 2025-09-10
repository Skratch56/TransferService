package org.skratch.transferservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchTransferRequest {
    @NotNull(message = "Transfers list is required")
    @NotEmpty(message = "Transfers list cannot be empty")
    @Valid
    private List<TransferRequest> transfers;
}
