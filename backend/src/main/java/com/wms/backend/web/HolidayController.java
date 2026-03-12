package com.wms.backend.web;

import com.wms.backend.domain.model.PublicHoliday;
import com.wms.backend.repo.PublicHolidayRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@Validated
public class HolidayController {

    private final PublicHolidayRepository holidayRepository;

    public HolidayController(PublicHolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public record CreateHolidayRequest(@NotNull LocalDate date, @NotBlank String lga, String description) {
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ROUTE_SUPERVISOR')")
    @PostMapping
    public ResponseEntity<PublicHoliday> create(@RequestBody CreateHolidayRequest req) {
        PublicHoliday h = new PublicHoliday();
        h.setHolidayDate(req.date());
        h.setLga(req.lga());
        h.setDescription(req.description());
        return ResponseEntity.ok(holidayRepository.save(h));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ROUTE_SUPERVISOR')")
    @GetMapping
    public ResponseEntity<List<PublicHoliday>> list() {
        return ResponseEntity.ok(holidayRepository.findAll());
    }
}

