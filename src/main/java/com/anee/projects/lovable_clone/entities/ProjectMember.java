package com.anee.projects.lovable_clone.entities;

import com.anee.projects.lovable_clone.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * <h2>Represents a user's membership in a project.</h2>
 * <i>The ProjectMember entity models the many-to-many relationship between users and projects,
 * capturing membership, roles, and invitation details:</i>
 * <hr>
 * <ul>
 *   <li>Each record links a {@link User} to a {@link Project} as a member.</li>
 *   <li>Defines the user's role in the project (e.g., EDITOR, VIEWER, OWNER).</li>
 *   <li>Tracks who invited the user and when the invitation occurred.</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code projectId}: Primary and Foreign key, references the associated project (part of composite primary key) linked to {@link ProjectMemberId}.</li>
 *   <li>{@code userId}: Primary and Foreign key, references the associated user (part of composite primary key) {@link ProjectMemberId}.</li>
 *   <li>{@code role}: The user's role in the project (e.g., EDITOR, VIEWER, OWNER) linked to enum {@link ProjectRole}.</li>
 *   <li>{@code invitedBy}: Foreign key, references the user who sent the invitation.</li>
 *   <li>{@code invitedAt}: Timestamp when the invitation was sent.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link Project} (each membership belongs to one project) maps to {@code projectId}.</li>
 *   <li>Many-to-one with {@link User} (each membership belongs to one user) maps to {@code userId}.</li>
 *   <li>Many-to-one with {@link User} as inviter (the user who invited this member).</li>
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
@Table(name = "project_members")
public class ProjectMember {

    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId")
    Project project;

    @ManyToOne
    @MapsId("userId")
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectRole projectRole;

    Instant invitedAt;
    Instant acceptedAt;
}
