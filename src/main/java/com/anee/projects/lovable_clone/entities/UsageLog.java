package com.anee.projects.lovable_clone.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * <h2>Represents a usage log entry for user actions within a project.</h2>
 * <i>The UsageLog entity tracks individual actions performed by users,
 * providing detailed analytics and auditing capabilities:</i>
 * <hr>
 * <ul>
 *   <li>Each log entry is associated with a specific {@link User} (via {@code user}).</li>
 *   <li>Each log entry is associated with a specific {@link Project} (via {@code project}).</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key, unique identifier for the log entry.</li>
 *   <li>{@code user}: The {@link User} who performed the action (foreign key).</li>
 *   <li>{@code project}: The {@link Project} where the action occurred (foreign key).</li>
 *   <li>{@code action}: Description or type of the action performed.</li>
 *   <li>{@code tokensUsed}: Number of tokens consumed by the action (for AI usage tracking).</li>
 *   <li>{@code durationMs}: Duration of the action in milliseconds.</li>
 *   <li>{@code metadata}: Additional metadata about the action (stored as JSON).</li>
 *   <li>{@code createdAt}: Timestamp when the log entry was created.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link User} (each log entry is performed by one user).</li>
 *   <li>Many-to-one with {@link Project} (each log entry is associated with one project).</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {

    Long id;
    User user;
    Project project;

    String action;

    Integer tokensUsed;
    Integer durationMs;

    String metadata; // JSON of {model_used, prompt_used}

    Instant createdAt;

}
