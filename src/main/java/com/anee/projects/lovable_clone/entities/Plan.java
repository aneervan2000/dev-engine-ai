package com.anee.projects.lovable_clone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * <h2>Represents a subscription plan in the system.</h2>
 * <i>Each plan defines the limits and features available to users,
 * such as the maximum number of projects, daily token usage, and preview limits.
 * Plans are referenced by {@link Subscription} entities.</i>
 * <hr>
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id} - Unique identifier for the plan</li>
 *   <li>{@code name} - Name of the plan</li>
 *   <li>{@code stripePriceId} - Stripe price identifier for billing</li>
 *   <li>{@code maxProjects} - Maximum number of projects allowed</li>
 *   <li>{@code maxTokensPerDay} - Maximum number of tokens allowed per day</li>
 *   <li>{@code maxPreviews} - Maximum number of previews allowed</li>
 *   <li>{@code unlimitedAi} - If true, unlimited access to LLM (ignore maxTokensPerDay)</li>
 *   <li>{@code active} - Whether the plan is currently active</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>One-to-Many: {@link Subscription} - A plan can be followed by many subscriptions ({@code plan_id} in {@link Subscription})</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(unique = true)
    String stripePriceId;

    Integer maxProjects;
    Integer maxTokensPerDay;
    Integer maxPreviews;
    Boolean unlimitedAi; // unlimited access to LLM, ignore maxTokensPerDay if true

    Boolean active;
}
