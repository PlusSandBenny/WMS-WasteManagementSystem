package com.wms.backend.service;

import com.wms.backend.domain.enums.PaymentMethod;
import com.wms.backend.domain.enums.PaymentStatus;
import com.wms.backend.domain.enums.PaymentType;
import com.wms.backend.domain.model.Invoice;
import com.wms.backend.domain.model.Payment;
import com.wms.backend.domain.model.User;
import com.wms.backend.repo.InvoiceRepository;
import com.wms.backend.repo.PaymentRepository;
import com.wms.backend.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class PaymentService {

    public record PaymentIntent(Long paymentId, String provider, String providerReference, String status) {
    }

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final BillingService billingService;
    private final RealtimeService realtimeService;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            UserRepository userRepository,
            BillingService billingService,
            RealtimeService realtimeService
    ) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.billingService = billingService;
        this.realtimeService = realtimeService;
    }

    @Transactional
    public PaymentIntent createIntent(Long userId, Long invoiceId, PaymentMethod method, String provider) {
        User user = userRepository.findById(userId).orElseThrow();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (!invoice.getAddress().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Invoice does not belong to user");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getAmountOwing());
        payment.setPaymentType(PaymentType.MONTHLY_FEE);
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider(provider == null ? "stub" : provider.toLowerCase());
        payment.setProviderReference(UUID.randomUUID().toString());

        Payment saved = paymentRepository.save(payment);
        return new PaymentIntent(saved.getId(), saved.getProvider(), saved.getProviderReference(), saved.getStatus().name());
    }

    @Transactional
    public Payment markCompletedByReference(String provider, String providerReference, String transactionId) {
        // Stub reconciliation: treat providerReference as unique and used as transaction ref.
        String normalizedProvider = provider == null ? "stub" : provider.toLowerCase();
        Payment payment = paymentRepository.findByProviderAndProviderReference(normalizedProvider, providerReference)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return payment;
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId != null ? transactionId : providerReference);
        payment.setPaidAt(Instant.now());
        Payment saved = paymentRepository.save(payment);

        Invoice invoice = payment.getInvoice();
        billingService.applyPayment(invoice, payment.getAmount());

        realtimeService.publishFinanceUnpaidChanged(invoice.getLga(), new FinanceUpdate(invoice.getId(), invoice.getYearMonth()));
        return saved;
    }

    public record FinanceUpdate(Long invoiceId, String yearMonth) {
    }
}
