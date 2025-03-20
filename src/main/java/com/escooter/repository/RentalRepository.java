package com.escooter.repository;

import com.escooter.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

    List<Rental> findByUserId(UUID userUUID);

    List<Rental> findByScooterId(UUID scooterUUID);
}
