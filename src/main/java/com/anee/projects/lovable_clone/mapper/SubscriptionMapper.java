package com.anee.projects.lovable_clone.mapper;

import com.anee.projects.lovable_clone.dto.subscription.PlanResponse;
import com.anee.projects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.anee.projects.lovable_clone.entities.Plan;
import com.anee.projects.lovable_clone.entities.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
