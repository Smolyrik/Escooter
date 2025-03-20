package com.escooter.repository;

import com.escooter.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    Optional<PaymentStatus> findByName(String name);
}
