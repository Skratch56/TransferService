package org.skratch.transferservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.model.Transfer;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Transfer toEntity(TransferRequest dto, String idempotencyKey);

    TransferResponse toTransferResponse(Transfer transfer);
}
