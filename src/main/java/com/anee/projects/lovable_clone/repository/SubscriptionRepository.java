package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.Subscription;
import com.anee.projects.lovable_clone.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Get the current active subscription for a user.
     * A user can only have one active subscription at a time, but may have multiple inactive subscriptions in the past. This method checks for any subscription with a status in the provided set (e.g., ACTIVE, PAST_DUE, TRIALING) to determine if the user currently has an active subscription.
     * @param userId
     * @param statusSet
     * @return
     */
    Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);

    boolean existsByStripeSubscriptionId(String subscriptionId);

   Optional<Subscription> findByStripeSubscriptionId(String gatewaySubscriptionId);
}
