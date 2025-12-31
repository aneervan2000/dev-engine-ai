package com.anee.projects.lovable_clone.dto.project;

import com.anee.projects.lovable_clone.dto.auth.UserProfileResponse;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) for detailed project information.
 * Represents a project with its ID, name, timestamps, and owner details.
 *
 * @param id        the unique identifier of the project
 * @param name      the name of the project
 * @param createdAt the timestamp when the project was created
 * @param updatedAt the timestamp when the project was last updated
 * @param owner     the owner of the project (UserProfileResponse)
 */
public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse owner
) {
}
