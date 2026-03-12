package com.wms.backend.repo;

import com.wms.backend.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);
    Optional<Payment> findByProviderAndProviderReference(String provider, String providerReference);
}
