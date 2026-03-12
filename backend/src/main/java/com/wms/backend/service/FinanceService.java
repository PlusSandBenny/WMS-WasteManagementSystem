package com.wms.backend.service;

import com.wms.backend.domain.model.Invoice;
import com.wms.backend.repo.InvoiceRepository;
import com.wms.backend.repo.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FinanceService {

    public record UnpaidRow(
            Long invoiceId,
            String address,
            String yearMonth,
            String lga,
            String amountOwing,
            long daysOverdue
    ) {
    }

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public FinanceService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Invoice> unpaidInvoices(String lga, YearMonth month) {
        return invoiceRepository.findByLgaAndYearMonthAndStatusNot(lga, month.toString(), Invoice.Status.PAID);
    }

    public long daysOverdue(Invoice invoice, LocalDate today) {
        if (invoice.getStatus() == Invoice.Status.PAID) {
            return 0;
        }
        if (!today.isAfter(invoice.getDueDate())) {
            return 0;
        }
        return ChronoUnit.DAYS.between(invoice.getDueDate(), today);
    }
}

