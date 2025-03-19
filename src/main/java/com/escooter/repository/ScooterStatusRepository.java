package com.escooter.repository;

import com.escooter.entity.ScooterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScooterStatusRepository extends JpaRepository<ScooterStatus, Integer> {

    Optional<ScooterStatus> findByName(String name);
}
