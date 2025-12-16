package com.anee.projects.lovable_clone.entities;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * <h2>Composite Primary Key for ProjectMember Entity : project_ownership in ER</h2>
 * <i>This class represents the composite key for the {@link ProjectMember} entity,
 * uniquely identifying a user's membership in a project.</i>
 * <hr>
 * <ul>
 *   <li>Combines {@code projectId} and {@code userId} as a composite primary key.</li>
 *   <li>Each combination of project and user is unique in the table {@link ProjectMember}.</li>
 *   <li>Used to map the many-to-many relationship between {@link User} and {@link Project}.</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code projectId}: Foreign key referencing the associated {@link Project}.</li>
 *   <li>{@code userId}: Foreign key referencing the associated {@link User}.</li>
 * </ol>
 *
 * <h6>Usage:</h6>
 * <ul>
 *   <li>Embedded as the primary key in the {@link ProjectMember} entity using {@code @EmbeddedId}.</li>
 *   <li>Ensures that each user can only have one membership record per project.</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberId {
    Long projectId;
    Long userId;
}
