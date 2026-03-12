package com.wms.backend.web;

import com.wms.backend.service.BillingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    public record RunMonthlyResponse(String yearMonth, int invoicesCreated) {
    }

    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('SUPER_ADMIN')")
    @PostMapping("/run-monthly")
    public ResponseEntity<RunMonthlyResponse> runMonthly(@RequestParam int year, @RequestParam int month) {
        YearMonth ym = YearMonth.of(year, month);
        int created = billingService.generateMonthlyInvoices(ym);
        return ResponseEntity.ok(new RunMonthlyResponse(ym.toString(), created));
    }
}

