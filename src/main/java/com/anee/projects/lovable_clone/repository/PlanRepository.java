package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
