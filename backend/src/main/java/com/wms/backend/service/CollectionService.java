package com.wms.backend.service;

import com.wms.backend.domain.enums.CollectionStatus;
import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.model.Address;
import com.wms.backend.domain.model.CollectionRecord;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.CollectionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CollectionService {

    public record TodayStatus(
            LocalDate date,
            BinType binType,
            CollectionStatus status,
            Instant actualCollectionTime
    ) {
    }

    private final AddressRepository addressRepository;
    private final CollectionRecordRepository recordRepository;
    private final RealtimeService realtimeService;

    public CollectionService(AddressRepository addressRepository, CollectionRecordRepository recordRepository, RealtimeService realtimeService) {
        this.addressRepository = addressRepository;
        this.recordRepository = recordRepository;
        this.realtimeService = realtimeService;
    }

    public Optional<CollectionRecord> findRecord(Long addressId, LocalDate date, BinType binType) {
        return recordRepository.findByAddressIdAndScheduledDateAndBinType(addressId, date, binType);
    }

    @Transactional
    public CollectionRecord markCollection(Long addressId, LocalDate scheduledDate, BinType binType, CollectionStatus status, Instant actualTime, String notes) {
        Address address = addressRepository.findById(addressId).orElseThrow();

        CollectionRecord record = recordRepository.findByAddressIdAndScheduledDateAndBinType(addressId, scheduledDate, binType)
                .orElseGet(() -> {
                    CollectionRecord r = new CollectionRecord();
                    r.setAddress(address);
                    r.setScheduledDate(scheduledDate);
                    r.setBinType(binType);
                    return r;
                });

        record.setStatus(status);
        record.setActualCollectionTime(actualTime);
        record.setNotes(notes);
        CollectionRecord saved = recordRepository.save(record);

        realtimeService.publishResidentStatusChanged(addressId, new TodayStatus(scheduledDate, binType, status, actualTime));
        return saved;
    }
}
