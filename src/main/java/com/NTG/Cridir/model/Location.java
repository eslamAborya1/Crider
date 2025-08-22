package com.NTG.Cridir.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "location")
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false) private
    Double latitude;
    @Column(nullable = false) private
    Double longitude;
    @Column(nullable = false) private
    OffsetDateTime timestamp;

    @PrePersist
    public void setDefaultTimestamp() {
        if (timestamp == null) timestamp = OffsetDateTime.now();
    }
}
