package com.wms.backend.domain.model;

import com.wms.backend.domain.BaseEntity;
import com.wms.backend.domain.enums.BinType;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "collection_schedules", indexes = {
        @Index(name = "idx_schedules_lga", columnList = "lga")
})
public class CollectionSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lga;

    @Enumerated(EnumType.STRING)
    @Column(name = "bin_type", nullable = false)
    private BinType binType;

    @Enumerated(EnumType.STRING)
    @Column(name = "collection_day", nullable = false)
    private DayOfWeek collectionDay;

    @Column(name = "collection_time", nullable = false)
    private LocalTime collectionTime;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public BinType getBinType() {
        return binType;
    }

    public void setBinType(BinType binType) {
        this.binType = binType;
    }

    public DayOfWeek getCollectionDay() {
        return collectionDay;
    }

    public void setCollectionDay(DayOfWeek collectionDay) {
        this.collectionDay = collectionDay;
    }

    public LocalTime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(LocalTime collectionTime) {
        this.collectionTime = collectionTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

