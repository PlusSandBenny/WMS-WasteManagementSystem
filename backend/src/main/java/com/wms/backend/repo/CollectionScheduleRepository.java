package com.wms.backend.repo;

import com.wms.backend.domain.model.CollectionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionScheduleRepository extends JpaRepository<CollectionSchedule, Long> {
    List<CollectionSchedule> findByLgaAndActiveTrue(String lga);
}

