package com.wms.backend.web;

import com.wms.backend.domain.enums.PaymentMethod;
import com.wms.backend.service.PaymentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record CreateIntentRequest(@NotNull Long invoiceId, @NotNull PaymentMethod method, String provider) {
    }

    @PostMapping("/intent")
    public ResponseEntity<PaymentService.PaymentIntent> intent(@RequestBody CreateIntentRequest req) {
        var principal = SecurityUtils.principal();
        return ResponseEntity.ok(paymentService.createIntent(principal.getId(), req.invoiceId(), req.method(), req.provider()));
    }

    // Development-only webhook to simulate provider reconciliation.
    public record StubWebhookRequest(@NotBlank String providerReference, String transactionId) {
    }

    @PostMapping("/webhook/stub")
    public ResponseEntity<String> stubWebhook(@RequestBody StubWebhookRequest req) {
        paymentService.markCompletedByReference("stub", req.providerReference(), req.transactionId());
        return ResponseEntity.ok("ok");
    }
}

