package com.NTG.Cridir.model;

import com.NTG.Cridir.model.Enum.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "service_request")
public class ServiceRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    private BigDecimal totalCost;

    @Column(nullable = false) private OffsetDateTime requestTime;
    private Long estimatedArrivalSeconds;

    @PrePersist
    public void setDefaults() {
        if (requestTime == null) requestTime = OffsetDateTime.now();
    }

    @Transient
    public Duration getEstimatedArrivalTime() {
        return estimatedArrivalSeconds != null ? Duration.ofSeconds(estimatedArrivalSeconds) : null;
    }

    public void setEstimatedArrivalTime(Duration duration) {
        this.estimatedArrivalSeconds = (duration != null) ? duration.getSeconds() : null;
    }
}
