package com.wms.backend.web;

import com.wms.backend.domain.enums.CollectionStatus;
import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.model.Address;
import com.wms.backend.domain.model.Invoice;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.InvoiceRepository;
import com.wms.backend.repo.UserRepository;
import com.wms.backend.security.UserPrincipal;
import com.wms.backend.service.CollectionService;
import com.wms.backend.service.ScheduleService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/me")
@Validated
public class MeController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final InvoiceRepository invoiceRepository;
    private final ScheduleService scheduleService;
    private final CollectionService collectionService;

    public MeController(
            UserRepository userRepository,
            AddressRepository addressRepository,
            InvoiceRepository invoiceRepository,
            ScheduleService scheduleService,
            CollectionService collectionService
    ) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.invoiceRepository = invoiceRepository;
        this.scheduleService = scheduleService;
        this.collectionService = collectionService;
    }

    public record MeResponse(Long id, String email, String phone, String role, AddressResponse address) {
    }

    public record AddressResponse(Long id, String state, String lga, String street, String houseNumber, String landmark, String display) {
    }

    @GetMapping
    public ResponseEntity<MeResponse> me() {
        UserPrincipal principal = SecurityUtils.principal();
        var user = userRepository.findById(principal.getId()).orElseThrow();
        var address = addressRepository.findByUserId(user.getId()).map(this::toAddressResponse).orElse(null);
        return ResponseEntity.ok(new MeResponse(user.getId(), user.getEmail(), user.getPhone(), user.getRole().name(), address));
    }

    public record UpdateAddressRequest(
            @NotBlank String state,
            @NotBlank String lga,
            @NotBlank String street,
            @NotBlank String houseNumber,
            String landmark
    ) {
    }

    @PutMapping("/address")
    public ResponseEntity<AddressResponse> upsertAddress(@RequestBody UpdateAddressRequest req) {
        UserPrincipal principal = SecurityUtils.principal();
        var user = userRepository.findById(principal.getId()).orElseThrow();

        Address address = addressRepository.findByUserId(user.getId()).orElseGet(Address::new);
        address.setState(req.state());
        address.setLga(req.lga());
        address.setStreet(req.street());
        address.setHouseNumber(req.houseNumber());
        address.setLandmark(req.landmark());
        address.setUser(user);
        Address saved = addressRepository.save(address);
        return ResponseEntity.ok(toAddressResponse(saved));
    }

    public record HomeResponse(
            String addressDisplay,
            LocalDate today,
            List<TodayPickupStatus> todayPickups,
            NextPickup nextPickup,
            InvoiceSummary invoice
    ) {
    }

    public record TodayPickupStatus(LocalDate date, String binType, String status, String actualCollectionTimeIso) {
    }

    public record NextPickup(LocalDate date, String binType, String time, boolean shiftedByHoliday) {
    }

    public record InvoiceSummary(Long invoiceId, String yearMonth, String status, String amountOwing, String dueDate) {
    }

    @GetMapping("/home")
    public ResponseEntity<HomeResponse> home() {
        UserPrincipal principal = SecurityUtils.principal();
        Address address = addressRepository.findByUserId(principal.getId()).orElseThrow(() -> new IllegalArgumentException("Address required"));

        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        ScheduleService.MonthSchedule monthSchedule = scheduleService.buildMonthSchedule(address.getLga(), ym);

        List<ScheduleService.Pickup> pickupsToday = monthSchedule.pickupsByDate().getOrDefault(today, List.of());
        List<TodayPickupStatus> todayStatuses = pickupsToday.isEmpty()
                ? List.of(new TodayPickupStatus(today, "NONE", "NOT_SCHEDULED", null))
                : pickupsToday.stream().map(p -> toTodayStatus(address.getId(), today, p.binType())).toList();

        NextPickup next = findNextPickup(today, address.getLga());

        Optional<Invoice> invoice = invoiceRepository.findByAddressIdAndYearMonth(address.getId(), ym.toString());
        InvoiceSummary invSummary = invoice
                .map(i -> new InvoiceSummary(i.getId(), i.getYearMonth(), i.getStatus().name(), i.getAmountOwing().toPlainString(), i.getDueDate().toString()))
                .orElse(null);

        return ResponseEntity.ok(new HomeResponse(address.getDisplay(), today, todayStatuses, next, invSummary));
    }

    private TodayPickupStatus toTodayStatus(Long addressId, LocalDate today, BinType binType) {
        return collectionService.findRecord(addressId, today, binType)
                .map(r -> new TodayPickupStatus(today, binType.name(), r.getStatus().name(), r.getActualCollectionTime() == null ? null : r.getActualCollectionTime().toString()))
                .orElse(new TodayPickupStatus(today, binType.name(), CollectionStatus.SCHEDULED.name(), null));
    }

    private NextPickup findNextPickup(LocalDate today, String lga) {
        YearMonth ym = YearMonth.from(today);
        for (int i = 0; i < 2; i++) {
            ScheduleService.MonthSchedule ms = scheduleService.buildMonthSchedule(lga, ym.plusMonths(i));
            var opt = ms.pickupsByDate().entrySet().stream()
                    .filter(e -> !e.getKey().isBefore(today))
                    .min(Comparator.comparing(Map.Entry::getKey));
            if (opt.isPresent()) {
                ScheduleService.Pickup p = opt.get().getValue().get(0);
                return new NextPickup(opt.get().getKey(), p.binType().name(), p.time().toString(), p.shiftedByHoliday());
            }
        }
        return null;
    }

    private AddressResponse toAddressResponse(Address a) {
        return new AddressResponse(a.getId(), a.getState(), a.getLga(), a.getStreet(), a.getHouseNumber(), a.getLandmark(), a.getDisplay());
    }
}
