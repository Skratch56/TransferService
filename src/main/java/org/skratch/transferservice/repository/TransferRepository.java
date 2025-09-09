package org.skratch.transferservice.repository;

import org.skratch.transferservice.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
    Optional<Transfer> findByTransferId(String transferId);
}
