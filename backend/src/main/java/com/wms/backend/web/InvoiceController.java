package com.wms.backend.web;

import com.wms.backend.domain.model.Invoice;
import com.wms.backend.repo.InvoiceRepository;
import com.wms.backend.security.UserPrincipal;
import com.wms.backend.service.PdfInvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final PdfInvoiceService pdfInvoiceService;

    public InvoiceController(InvoiceRepository invoiceRepository, PdfInvoiceService pdfInvoiceService) {
        this.invoiceRepository = invoiceRepository;
        this.pdfInvoiceService = pdfInvoiceService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        UserPrincipal principal = SecurityUtils.principal();
        boolean isOwner = invoice.getAddress().getUser() != null && invoice.getAddress().getUser().getId().equals(principal.getId());
        boolean privileged = principal.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_FINANCE_OFFICER") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!isOwner && !privileged) {
            throw new IllegalArgumentException("Not allowed");
        }
        byte[] pdf = pdfInvoiceService.renderInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice-" + invoiceId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
