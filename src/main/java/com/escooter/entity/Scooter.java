package com.escooter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "scooter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scooter {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "rental_point_id", referencedColumnName = "id")
    private RentalPoint rentalPoint;

    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "pricing_plan_id", referencedColumnName = "id")
    private PricingPlan pricingPlan;

    @Column(name = "battery_level", nullable = false)
    private BigDecimal batteryLevel;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private ScooterStatus status;

    @Column(name = "mileage", nullable = false)
    private BigDecimal mileage;
}
