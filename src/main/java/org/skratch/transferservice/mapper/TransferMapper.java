package org.skratch.transferservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.skratch.transferservice.dto.TransferRequest;
import org.skratch.transferservice.dto.TransferResponse;
import org.skratch.transferservice.model.Transfer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransferMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferId", ignore = true)   // generated in service
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Transfer toEntity(TransferRequest dto, String idempotencyKey);

    TransferResponse toTransferResponse(Transfer transfer);
}
