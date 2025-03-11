package com.escooter.repository;

import com.escooter.entity.RentalPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RentalPointRepository extends JpaRepository<RentalPoint, UUID> {
}
