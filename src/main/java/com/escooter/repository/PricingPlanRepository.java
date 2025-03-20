package com.escooter.repository;

import com.escooter.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, UUID> {
}
