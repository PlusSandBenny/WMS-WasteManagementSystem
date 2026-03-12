package com.wms.backend.repo;

import com.wms.backend.domain.model.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wms.backend.domain.enums.BinType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {
    Optional<CollectionRecord> findByAddressIdAndScheduledDateAndBinType(Long addressId, LocalDate scheduledDate, BinType binType);
    List<CollectionRecord> findByAddressIdAndScheduledDateBetween(Long addressId, LocalDate start, LocalDate end);
}
