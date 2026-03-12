package com.wms.backend.service;

import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.model.CollectionSchedule;
import com.wms.backend.domain.model.PublicHoliday;
import com.wms.backend.repo.CollectionScheduleRepository;
import com.wms.backend.repo.PublicHolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ScheduleServiceHolidayShiftTest {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    CollectionScheduleRepository scheduleRepository;

    @Autowired
    PublicHolidayRepository holidayRepository;

    @BeforeEach
    void setup() {
        holidayRepository.deleteAll();
        scheduleRepository.deleteAll();
    }

    @Test
    void shiftsEntireWeekByOneDayWhenHolidayMarked() {
        String lga = "Ikeja";
        YearMonth month = YearMonth.of(2026, 3);

        seedRule(lga, BinType.GENERAL_WASTE, DayOfWeek.MONDAY, LocalTime.of(6, 0));
        seedRule(lga, BinType.RECYCLING, DayOfWeek.WEDNESDAY, LocalTime.of(6, 0));

        // Pick a Wednesday mid-month to avoid month-edge effects.
        LocalDate holiday = LocalDate.of(2026, 3, 15).with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        PublicHoliday h = new PublicHoliday();
        h.setLga(lga);
        h.setHolidayDate(holiday);
        h.setDescription("Test Holiday");
        holidayRepository.save(h);

        ScheduleService.MonthSchedule schedule = scheduleService.buildMonthSchedule(lga, month);

        LocalDate weekStart = holiday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Monday pickup shifts to Tuesday.
        assertTrue(schedule.pickupsByDate().getOrDefault(weekStart, java.util.List.of()).isEmpty());
        assertTrue(
                schedule.pickupsByDate().getOrDefault(weekStart.plusDays(1), java.util.List.of()).stream()
                        .anyMatch(p -> p.binType() == BinType.GENERAL_WASTE && p.shiftedByHoliday()),
                "Expected GENERAL_WASTE to shift from Monday to Tuesday"
        );

        // Wednesday pickup shifts to Thursday.
        assertTrue(
                schedule.pickupsByDate().getOrDefault(weekStart.plusDays(3), java.util.List.of()).stream()
                        .anyMatch(p -> p.binType() == BinType.RECYCLING && p.shiftedByHoliday()),
                "Expected RECYCLING to shift from Wednesday to Thursday"
        );
    }

    private void seedRule(String lga, BinType binType, DayOfWeek day, LocalTime time) {
        CollectionSchedule s = new CollectionSchedule();
        s.setLga(lga);
        s.setBinType(binType);
        s.setCollectionDay(day);
        s.setCollectionTime(time);
        s.setActive(true);
        scheduleRepository.save(s);
    }
}

