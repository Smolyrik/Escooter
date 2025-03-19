package com.escooter.repository;

import com.escooter.entity.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalTypeRepository extends JpaRepository<RentalType, Integer> {

    Optional<RentalType> findByName(String name);
}
