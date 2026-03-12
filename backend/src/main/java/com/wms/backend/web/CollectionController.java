package com.wms.backend.web;

import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.enums.CollectionStatus;
import com.wms.backend.service.CollectionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/collections")
@Validated
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public record MarkRequest(
            @NotNull LocalDate scheduledDate,
            @NotNull BinType binType,
            @NotNull CollectionStatus status,
            String actualCollectionTimeIso,
            String notes
    ) {
    }

    @PreAuthorize("hasRole('ROUTE_SUPERVISOR') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{addressId}/mark")
    public ResponseEntity<String> mark(@PathVariable Long addressId, @RequestBody MarkRequest req) {
        Instant actual = req.actualCollectionTimeIso() == null ? null : Instant.parse(req.actualCollectionTimeIso());
        collectionService.markCollection(addressId, req.scheduledDate(), req.binType(), req.status(), actual, req.notes());
        return ResponseEntity.ok("ok");
    }
}

