package com.escooter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rental")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "scooter_id", referencedColumnName = "id", nullable = false)
    private Scooter scooter;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private RentalStatus status;

    @ManyToOne
    @JoinColumn(name = "rental_type_id", referencedColumnName = "id")
    private RentalType rentalType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "distance")
    private BigDecimal distance;
}
