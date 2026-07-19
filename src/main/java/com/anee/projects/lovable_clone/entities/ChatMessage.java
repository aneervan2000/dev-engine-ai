package com.anee.projects.lovable_clone.entities;

import com.anee.projects.lovable_clone.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * <h2>Represents a single message within a chat session.</h2>
 * <i>The ChatMessage entity models an individual message exchanged in a chat session between a user and the system (or other users):</i>
 * <hr>
 * <ul>
 *   <li>Each chat message is uniquely identified by its {@code id}.</li>
 *   <li>Each message belongs to a specific {@link ChatSession}, identified by {@code projectId} and {@code userId}.</li>
 *   <li>Each message is associated with a {@link Project} and a {@link User} (the sender).</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key for the chat message.</li>
 *   <li>{@code projectId}: Foreign key referencing the associated project (part of the session context).</li>
 *   <li>{@code userId}: Foreign key referencing the user who sent the message (part of the session context).</li>
 *   <li>{@code role}: The role of the sender (e.g., USER, ASSISTANT, SYSTEM, TOOL) linked to {@link MessageRole}.</li>
 *   <li>{@code content}: The text content of the message.</li>
 *   <li>{@code toolCalls}: JSON field containing tool call data (if any tools were invoked).</li>
 *   <li>{@code toolCallId}: Identifier for a specific tool call (if applicable).</li>
 *   <li>{@code tokensUsed}: Number of tokens used in this message (for billing/usage tracking).</li>
 *   <li>{@code createdAt}: Timestamp when the message was created.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link ChatSession} (each message belongs to one session).</li>
 *   <li>Many-to-one with {@link Project} (each message is associated with one project).</li>
 *   <li>Many-to-one with {@link User} (each message is sent by one user).</li>
 * </ul>
 */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    ChatSession chatSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role; // USER, ASSISTANT

    @Column(columnDefinition = "text",  nullable = false)
    String content;

    Integer tokensUsed = 0;

//    String toolCalls; // JSON Array of Tools called

    @CreationTimestamp
    Instant createdAt;
}
