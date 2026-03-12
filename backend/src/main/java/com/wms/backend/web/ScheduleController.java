package com.wms.backend.web;

import com.wms.backend.domain.model.Address;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final AddressRepository addressRepository;
    private final ScheduleService scheduleService;

    public ScheduleController(AddressRepository addressRepository, ScheduleService scheduleService) {
        this.addressRepository = addressRepository;
        this.scheduleService = scheduleService;
    }

    public record PickupDto(String binType, String time, boolean shiftedByHoliday) {
    }

    public record MonthScheduleResponse(String month, Map<String, List<PickupDto>> pickupsByDate, List<String> notices) {
    }

    @GetMapping("/month")
    public ResponseEntity<MonthScheduleResponse> month(@RequestParam int year, @RequestParam int month) {
        var principal = SecurityUtils.principal();
        Address address = addressRepository.findByUserId(principal.getId()).orElseThrow(() -> new IllegalArgumentException("Address required"));
        YearMonth ym = YearMonth.of(year, month);

        ScheduleService.MonthSchedule schedule = scheduleService.buildMonthSchedule(address.getLga(), ym);
        Map<String, List<PickupDto>> map = new TreeMap<>();
        for (Map.Entry<LocalDate, List<ScheduleService.Pickup>> e : schedule.pickupsByDate().entrySet()) {
            map.put(e.getKey().toString(), e.getValue().stream()
                    .map(p -> new PickupDto(p.binType().name(), p.time().toString(), p.shiftedByHoliday()))
                    .toList());
        }

        return ResponseEntity.ok(new MonthScheduleResponse(ym.toString(), map, schedule.notices()));
    }
}

