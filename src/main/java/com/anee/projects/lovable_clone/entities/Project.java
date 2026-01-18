package com.anee.projects.lovable_clone.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * <h2>Represents a project within the system.</h2>
 * <i>The Project entity is a core part of the application,
 * representing a workspace or collection of resources:</i>
 * <hr>
 * <ul>
 *   <li>Each project is owned by a single {@link User} (via {@code ownerId}).</li>
 *   <li>Projects can have multiple members (via {@link ProjectMember}).</li>
 *   <li>Projects can contain multiple files (via {@link ProjectFile}).</li>
 *   <li>Projects can have multiple chat sessions (via {@link ChatSession}).</li>
 *   <li>Each project can have one active {@link Preview} at a time.</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key, unique identifier for the project.</li>
 *   <li>{@code name}: Name of the project.</li>
 *   <li>{@code ownerId}: Foreign key referencing the owning {@link User}.</li>
 *   <li>{@code isPublic}: Indicates if the project is publicly accessible.</li>
 *   <li>{@code createdAt}: Timestamp when the project was created.</li>
 *   <li>{@code updatedAt}: Timestamp when the project was last updated.</li>
 *   <li>{@code deletedAt}: Timestamp when the project was soft-deleted (null if active).</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link User} ({@code owner}).</li>
 *   <li>One-to-many with {@link ProjectMember} (memberships).</li>
 *   <li>One-to-many with {@link ProjectFile} (files contained in the project).</li>
 *   <li>One-to-many with {@link ChatSession} (chat sessions associated with the project).</li>
 *   <li>One-to-one with {@link Preview} (active preview for the project).</li>
 * </ul>
 *
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "projects",
        indexes =  {
            @Index(name = "index_projects_updated_at_desc", columnList = "updated_at DESC, deleted_at ASC"),
            @Index(name = "index_projects_deleted_at", columnList = "deleted_at")
        })
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false) // project name cannot be null
    String name;

    Boolean isPublic = false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft delete
}
