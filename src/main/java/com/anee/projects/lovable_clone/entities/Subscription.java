package com.anee.projects.lovable_clone.entities;

import com.anee.projects.lovable_clone.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
/**
 * <h2>Represents a user's subscription to a specific plan.</h2>
 * <i>Each subscription is linked to a {@link User} and a {@link Plan}, and tracks the current billing period,
 * status, and Stripe subscription integration. Subscriptions are used to manage access to features and quotas
 * defined by the associated plan.</i>
 * <hr>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id} - Primary key.</li>
 *   <li>{@code user_id} - Foreign key to the user who owns the subscription.</li>
 *   <li>{@code plan_id} - Foreign key to the subscribed plan.</li>
 *   <li>{@code stripe_subscription_id} - Unique Stripe subscription identifier.</li>
 *   <li>{@code status} - Current status of the subscription (e.g., active, canceled) linked to enum {@link SubscriptionStatus}.</li>
 *   <li>{@code current_period_start} - Start timestamp of the current billing period.</li>
 *   <li>{@code current_period_end} - End timestamp of the current billing period.</li>
 *   <li>{@code cancel_at_period_end} - Indicates if the subscription will cancel at the end of the period.</li>
 *   <li>{@code created_at} - Timestamp when the subscription was created.</li>
 *   <li>{@code updated_at} - Timestamp when the subscription was last updated.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link User}: Each subscription belongs to one user ({@code user_id}).</li>
 *   <li>Many-to-one with {@link Plan}: Each subscription is for one plan ({@code plan_id}).</li>
 *   <li>References a Stripe subscription via {@code stripe_subscription_id}.</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    Long id;

    User user;
    Plan plan;

    String stripeSubscriptionId;

    SubscriptionStatus status;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    Boolean cancelAtPeriodEnd = false;

    Instant createdAt;
    Instant updatedAt;
}
