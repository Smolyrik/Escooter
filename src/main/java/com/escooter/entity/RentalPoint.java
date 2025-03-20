package com.escooter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rental_point")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalPoint {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = false)
    private User manager;
}
