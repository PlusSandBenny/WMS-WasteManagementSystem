package com.wms.backend.service;

import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.model.CollectionSchedule;
import com.wms.backend.domain.model.PublicHoliday;
import com.wms.backend.repo.CollectionScheduleRepository;
import com.wms.backend.repo.PublicHolidayRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ScheduleService {

    public record Pickup(BinType binType, LocalTime time, boolean shiftedByHoliday) {
    }

    public record MonthSchedule(YearMonth month, Map<LocalDate, List<Pickup>> pickupsByDate, List<String> notices) {
    }

    private final CollectionScheduleRepository scheduleRepository;
    private final PublicHolidayRepository holidayRepository;

    public ScheduleService(CollectionScheduleRepository scheduleRepository, PublicHolidayRepository holidayRepository) {
        this.scheduleRepository = scheduleRepository;
        this.holidayRepository = holidayRepository;
    }

    public MonthSchedule buildMonthSchedule(String lga, YearMonth month) {
        List<CollectionSchedule> rules = scheduleRepository.findByLgaAndActiveTrue(lga);

        Map<LocalDate, List<Pickup>> base = new HashMap<>();
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            for (CollectionSchedule rule : rules) {
                if (rule.getCollectionDay() == dow) {
                    base.computeIfAbsent(d, __ -> new ArrayList<>())
                            .add(new Pickup(rule.getBinType(), rule.getCollectionTime(), false));
                }
            }
        }

        // Holiday week-shift: if a public holiday occurs in a week, shift all pickups in that week by +1 day.
        List<PublicHoliday> holidays = holidayRepository.findByLgaAndHolidayDateBetween(lga, start, end);
        Set<LocalDate> holidayDates = new HashSet<>();
        for (PublicHoliday h : holidays) {
            holidayDates.add(h.getHolidayDate());
        }

        Map<LocalDate, List<Pickup>> adjusted = base;
        List<String> notices = new ArrayList<>();
        if (!holidayDates.isEmpty()) {
            adjusted = applyWeekShift(base, holidayDates, notices);
        }

        adjusted.replaceAll((k, v) -> {
            v.sort(Comparator.comparing(Pickup::time));
            return v;
        });

        return new MonthSchedule(month, adjusted, notices);
    }

    private Map<LocalDate, List<Pickup>> applyWeekShift(
            Map<LocalDate, List<Pickup>> base,
            Set<LocalDate> holidayDates,
            List<String> notices
    ) {
        Map<LocalDate, List<Pickup>> out = new HashMap<>();

        // Identify weeks (Mon-Sun) containing at least one holiday.
        Set<LocalDate> weekStarts = new HashSet<>();
        for (LocalDate holiday : holidayDates) {
            LocalDate weekStart = holiday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weekStarts.add(weekStart);
        }

        for (Map.Entry<LocalDate, List<Pickup>> e : base.entrySet()) {
            LocalDate date = e.getKey();
            LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            boolean shift = weekStarts.contains(weekStart);
            LocalDate target = shift ? date.plusDays(1) : date;
            for (Pickup p : e.getValue()) {
                out.computeIfAbsent(target, __ -> new ArrayList<>())
                        .add(new Pickup(p.binType, p.time, shift || p.shiftedByHoliday));
            }
        }

        if (!weekStarts.isEmpty()) {
            List<LocalDate> sorted = new ArrayList<>(weekStarts);
            sorted.sort(Comparator.naturalOrder());
            for (LocalDate ws : sorted) {
                notices.add("Public holiday in week starting " + ws + " -> schedule shifted by +1 day for that week");
            }
        }

        return out;
    }
}

