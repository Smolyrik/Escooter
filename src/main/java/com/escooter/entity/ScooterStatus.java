package com.escooter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scooter_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScooterStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
