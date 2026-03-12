package com.wms.backend.web;

import com.wms.backend.domain.model.Invoice;
import com.wms.backend.service.FinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    public record UnpaidRowDto(
            Long invoiceId,
            String address,
            String owingNgn,
            long daysOverdue,
            String status
    ) {
    }

    public record UnpaidDashboardResponse(
            String lga,
            String yearMonth,
            String totalOwedNgn,
            String totalCollectedNgn,
            List<UnpaidRowDto> rows
    ) {
    }

    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('SUPER_ADMIN')")
    @GetMapping("/unpaid")
    public ResponseEntity<UnpaidDashboardResponse> unpaid(
            @RequestParam String lga,
            @RequestParam int year,
            @RequestParam int month
    ) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate today = LocalDate.now();

        List<Invoice> invoices = financeService.unpaidInvoices(lga, ym);
        List<UnpaidRowDto> rows = invoices.stream().map(inv -> new UnpaidRowDto(
                inv.getId(),
                inv.getAddress().getDisplay(),
                inv.getAmountOwing().toPlainString(),
                financeService.daysOverdue(inv, today),
                inv.getStatus().name()
        )).toList();

        BigDecimal totalOwed = invoices.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCollected = invoices.stream().map(Invoice::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(new UnpaidDashboardResponse(lga, ym.toString(), totalOwed.toPlainString(), totalCollected.toPlainString(), rows));
    }
}
