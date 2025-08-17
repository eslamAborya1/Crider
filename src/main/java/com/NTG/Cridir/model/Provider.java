package com.NTG.Cridir.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "provider")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_provider_user"))
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private boolean availabilityStatus = true;

    @ManyToOne
    @JoinColumn(name = "current_location_id",
            foreignKey = @ForeignKey(name = "fk_provider_location"))
    private Location currentLocation;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequest> serviceRequests;
}
