package com.anee.projects.lovable_clone.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * <h2>Represents a user in the system.</h2>
 * <i>The User entity is central to the application and is associated with several other entities:</i>
 * <hr>
 * <ul>
 *   <li>Has one active {@link Subscription}</li>
 *   <li>Performs many {@link UsageLog} actions</li>
 *   <li>Owns many {@link Project} instances (via {@link ProjectMember})</li>
 *   <li>Is a member of many {@link Project} instances (via {@link ProjectMember})</li>
 *   <li>Participates in many {@link ChatSession} conversations</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key, unique identifier for the user.</li>
 *   <li>{@code email}: Unique email address for login and identification.</li>
 *   <li>{@code password_hash}: Hashed password for authentication.</li>
 *   <li>{@code name}: Display name of the user.</li>
 *   <li>{@code avatar_url}: URL to the user's avatar image.</li>
 *   <li>{@code created_at}: Timestamp when the user was created.</li>
 *   <li>{@code updated_at}: Timestamp when the user was last updated.</li>
 *   <li>{@code deleted_at}: Timestamp when the user was soft-deleted (null if active).</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>One-to-many with {@link Subscription} (user can have multiple subscriptions, but only one active at a time).</li>
 *   <li>One-to-many with {@link UsageLog} (tracks user actions and usage).</li>
 *   <li>One-to-many with {@link Project} (as owner or member).</li>
 *   <li>One-to-many with {@link ChatSession} (user can participate in multiple chat sessions).</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users") // "user" is a reserved keyword in many SQL databases
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;
    String passwordHash;
    String name;

    String avatarUrl;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft delete
}
