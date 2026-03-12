package com.wms.backend.repo;

import com.wms.backend.domain.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {
    List<PublicHoliday> findByLgaAndHolidayDateBetween(String lga, LocalDate start, LocalDate end);
}

