package com.wms.backend.service;

import com.wms.backend.config.AppProperties;
import com.wms.backend.domain.model.Address;
import com.wms.backend.domain.model.Invoice;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.InvoiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    private final AddressRepository addressRepository;
    private final InvoiceRepository invoiceRepository;

    private final BigDecimal defaultMonthlyFee;

    public BillingService(
            AddressRepository addressRepository,
            InvoiceRepository invoiceRepository,
            AppProperties appProperties
    ) {
        this.addressRepository = addressRepository;
        this.invoiceRepository = invoiceRepository;
        this.defaultMonthlyFee = appProperties.getBilling().getDefaultMonthlyFeeNgn();
    }

    @Transactional
    public int generateMonthlyInvoices(YearMonth month) {
        String ym = month.toString(); // YYYY-MM
        List<Address> addresses = addressRepository.findAll();
        int created = 0;
        for (Address address : addresses) {
            if (invoiceRepository.findByAddressIdAndYearMonth(address.getId(), ym).isPresent()) {
                continue;
            }
            Invoice invoice = new Invoice();
            invoice.setAddress(address);
            invoice.setYearMonth(ym);
            invoice.setTotalAmount(defaultMonthlyFee);
            invoice.setAmountPaid(BigDecimal.ZERO);
            invoice.setStatus(Invoice.Status.UNPAID);
            invoice.setDueDate(month.atDay(1).plusDays(7));
            invoiceRepository.save(invoice);
            created++;
        }
        return created;
    }

    @Transactional
    public Invoice applyPayment(Invoice invoice, BigDecimal amount) {
        invoice.setAmountPaid(invoice.getAmountPaid().add(amount));
        if (invoice.getAmountPaid().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(Invoice.Status.PAID);
        } else if (invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.Status.PARTIALLY_PAID);
        } else {
            invoice.setStatus(Invoice.Status.UNPAID);
        }
        return invoiceRepository.save(invoice);
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
