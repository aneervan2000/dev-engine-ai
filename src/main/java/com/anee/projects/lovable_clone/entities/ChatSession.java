package com.anee.projects.lovable_clone.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * <h2>Represents a chat session within a project.</h2>
 * <i>The ChatSession entity models a conversation between a user and the system (or other users) within a specific project context:</i>
 * <hr>
 * <ul>
 *   <li>Each chat session is uniquely identified by a composite key: {@code projectId} and {@code userId}.</li>
 *   <li>Each chat session belongs to a single {@link Project} and a single {@link User}.</li>
 *   <li>Each chat session can contain multiple {@link ChatMessage} instances (the conversation history).</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code projectId}: Foreign key referencing the associated project (part of the primary key).</li>
 *   <li>{@code userId}: Foreign key referencing the user who owns/initiated the session (part of the primary key).</li>
 *   <li>{@code title}: Optional title or label for the chat session.</li>
 *   <li>{@code createdAt}: Timestamp when the chat session was created.</li>
 *   <li>{@code updatedAt}: Timestamp when the chat session was last updated.</li>
 *   <li>{@code deletedAt}: Timestamp when the chat session was soft-deleted (null if active).</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link Project} (each session belongs to one project).</li>
 *   <li>Many-to-one with {@link User} (each session belongs to one user).</li>
 *   <li>One-to-many with {@link ChatMessage} (each session contains multiple messages).</li>
 * </ul>
 */

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {

    @EmbeddedId
    private ChatSessionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft delete
}
