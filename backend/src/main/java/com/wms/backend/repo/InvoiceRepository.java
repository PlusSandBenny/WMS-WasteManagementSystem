package com.wms.backend.repo;

import com.wms.backend.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByAddressIdAndYearMonth(Long addressId, String yearMonth);
    List<Invoice> findByLgaAndYearMonth(String lga, String yearMonth);
    List<Invoice> findByLgaAndYearMonthAndStatusNot(String lga, String yearMonth, Invoice.Status status);
}

