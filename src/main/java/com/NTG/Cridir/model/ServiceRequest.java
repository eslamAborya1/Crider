package com.NTG.Cridir.model;

import com.NTG.Cridir.model.Enum.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;


@Entity
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_request_customer"))
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "provider_id",
            foreignKey = @ForeignKey(name = "fk_service_request_provider"))
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_request_location"))
    private Location location;

    @Column(nullable = false)
    private String issueType;

    @Column(nullable = false)
    private String carType;

    @Column(nullable = false)
    private Integer carModelYear;

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalCost;

    @Column(nullable = false)
    private OffsetDateTime requestTime;

    private Long estimatedArrivalSeconds;

    @Transient
    public Duration getEstimatedArrivalTime() {
        return estimatedArrivalSeconds != null ? Duration.ofSeconds(estimatedArrivalSeconds) : null;
    }

    public void setEstimatedArrivalTime(Duration duration) {
        this.estimatedArrivalSeconds = (duration != null) ? duration.getSeconds() : null;
    }

    @PrePersist
    public void setDefaults() {
        if (requestTime == null) {
            requestTime = OffsetDateTime.now();
        }
    }


    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public OffsetDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(OffsetDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public Long getEstimatedArrivalSeconds() {
        return estimatedArrivalSeconds;
    }

    public void setEstimatedArrivalSeconds(Long estimatedArrivalSeconds) {
        this.estimatedArrivalSeconds = estimatedArrivalSeconds;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getCarModelYear() {
        return carModelYear;
    }

    public void setCarModelYear(Integer carModelYear) {
        this.carModelYear = carModelYear;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
