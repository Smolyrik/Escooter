package com.escooter.repository;

import com.escooter.entity.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, UUID> {

    boolean existsByIdAndStatus_Name(UUID scooterId, String status);

    List<Scooter> findByRentalPointId(UUID rentalPointId);

    @Query("SELECT s FROM Scooter s WHERE s.rentalPoint.id = :rentalPointId AND s.status.name = 'Rented'")
    List<Scooter> getRentedScootersByRentalPointId(@Param("rentalPointId") UUID rentalPointId);

    @Query("SELECT s FROM Scooter s WHERE s.rentalPoint.id = :rentalPointId AND s.status.name = 'Available'")
    List<Scooter> getAvailableScootersByRentalPointId(@Param("rentalPointId") UUID rentalPointId);

    @Query("SELECT s FROM Scooter s WHERE s.rentalPoint.id = :rentalPointId AND s.status.name = 'In Repair'")
    List<Scooter> getInRepairScootersByRentalPointId(@Param("rentalPointId") UUID rentalPointId);


}
