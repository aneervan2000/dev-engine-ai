package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {

    List<PlanResponse> getAllActivePlans();
}
