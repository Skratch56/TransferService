package org.skratch.transferservice.repository;

import org.skratch.transferservice.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
    Optional<Transfer> findByTransferId(String transferId);
    List<Transfer> findAll();
}
