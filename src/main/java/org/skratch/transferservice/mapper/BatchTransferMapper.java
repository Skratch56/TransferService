//package org.skratch.transferservice.mapper;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingConstants;
//import org.skratch.transferservice.dto.BatchTransferResponse;
//import org.skratch.transferservice.dto.TransferRequest;
//import org.skratch.transferservice.dto.TransferResponse;
//import org.skratch.transferservice.model.Transfer;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface BatchTransferMapper {
//    // Map DTO -> Entity (idempotencyKey is provided at runtime)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "transferId", ignore = true)   // generated in service
//    @Mapping(target = "status", constant = "PENDING")
//    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
//    Transfer toEntity(TransferRequest dto, String idempotencyKey);
//
//    // Bulk map DTOs -> Entities
////    List<Transfer> toEntityList(List<TransferRequest> requests, String idempotencyKey);
//
//    // Entity -> Response DTO
//    TransferResponse toTransferResponse(Transfer transfer);
//
//    // Bulk map Entities -> Response DTOs
//    List<TransferResponse> toResponseList(List<Transfer> transfers);
//
//    // Wrap responses into BatchTransferResponse
//    default BatchTransferResponse toBatchResponse(List<Transfer> transfers) {
//        return BatchTransferResponse.builder()
//                .results(toResponseList(transfers))
//                .build();
//    }
//}
