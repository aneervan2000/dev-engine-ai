package com.anee.projects.lovable_clone.entities;

import com.anee.projects.lovable_clone.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a single event that belongs to a chat message.
 *
 * <p>Persistence:
 * - Annotated with `@Entity` and mapped to the `chat_events` table.
 * - `id` is the primary key and is auto-generated using the IDENTITY strategy.
 *
 * <p>Relationships:
 * - Many-to-one relationship to `ChatMessage` (`chatMessage`): multiple `ChatEvent` instances
 *   can belong to a single chat message. The association is lazy-loaded and non-null.
 *
 * <p>Fields:
 * - `type` stores the kind of event using the `ChatEventType` enum and is persisted as a string.
 * - `sequenceOrder` is an integer used to order events for the same chat message.
 * - `content` holds textual content and is stored using a `TEXT` column type to allow large bodies.
 * - `filePath` contains an optional path to a related file (if any).
 * - `metadata` is an optional free-form text column for extra structured/unstructured data
 *   (JSON or other serialized form) and uses `text` column definition.
 *
 * <p>Tooling:
 * - Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
 *   reduce boilerplate by generating accessors, constructors and a builder.
 * - `@FieldDefaults(level = AccessLevel.PRIVATE)` makes fields private by default.
 *
 * <p>Usage:
 * - Use `sequenceOrder` to reconstruct the chronological order of events for a message.
 * - Use `type` to switch behavior depending on whether the event is a send, edit, delete,
 *   attachment, or another domain-specific event defined in `ChatEventType`.
 */
@Entity
@Table(name = "chat_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatEvent {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    // For a single chat message there can be many chat events
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    ChatMessage chatMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChatEventType type;

    @Column(nullable = false)
    Integer sequenceOrder;

    @Column(columnDefinition = "TEXT")
    String content;

    String filePath;  // NULL unless FILE_EDIT

    @Column(columnDefinition = "text")
    String metadata;
}
