package com.wms.backend.domain.model;

import com.wms.backend.domain.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoices_lga_month", columnList = "lga,year_month"),
        @Index(name = "idx_invoices_address_month", columnList = "address_id,year_month")
})
public class Invoice extends BaseEntity {

    public enum Status {
        UNPAID,
        PARTIALLY_PAID,
        PAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "lga", nullable = false)
    private String lga;

    @Column(name = "year_month", nullable = false)
    private String yearMonth; // YYYY-MM

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.UNPAID;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    public Long getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.lga = address.getLga();
    }

    public String getLga() {
        return lga;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Transient
    public BigDecimal getAmountOwing() {
        BigDecimal owing = totalAmount.subtract(amountPaid);
        return owing.max(BigDecimal.ZERO);
    }
}

