package com.wms.backend.service;

import com.wms.backend.domain.enums.Role;
import com.wms.backend.domain.model.Address;
import com.wms.backend.domain.model.Invoice;
import com.wms.backend.domain.model.User;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.InvoiceRepository;
import com.wms.backend.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FinanceServiceUnpaidTest {

    @Autowired
    FinanceService financeService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        invoiceRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void returnsUnpaidInvoicesByLgaAndMonth() {
        User resident = new User();
        resident.setEmail("resident1@example.com");
        resident.setRole(Role.RESIDENT);
        resident.setPasswordHash("x");
        resident = userRepository.save(resident);

        Address address = new Address();
        address.setUser(resident);
        address.setState("Lagos");
        address.setLga("Ikeja");
        address.setStreet("Allen Avenue");
        address.setHouseNumber("12B");
        address.setLandmark("Near XYZ");
        address = addressRepository.save(address);

        Invoice inv = new Invoice();
        inv.setAddress(address);
        inv.setYearMonth("2026-03");
        inv.setTotalAmount(new BigDecimal("2000.00"));
        inv.setAmountPaid(BigDecimal.ZERO);
        inv.setStatus(Invoice.Status.UNPAID);
        inv.setDueDate(LocalDate.of(2026, 3, 8));
        invoiceRepository.save(inv);

        List<Invoice> unpaid = financeService.unpaidInvoices("Ikeja", YearMonth.of(2026, 3));
        assertEquals(1, unpaid.size());
        assertEquals("Ikeja", unpaid.get(0).getLga());
        assertEquals(new BigDecimal("2000.00"), unpaid.get(0).getAmountOwing());

        long daysOverdue = financeService.daysOverdue(unpaid.get(0), LocalDate.of(2026, 3, 12));
        assertEquals(4, daysOverdue);
    }
}

