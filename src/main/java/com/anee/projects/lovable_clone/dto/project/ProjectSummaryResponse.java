package com.anee.projects.lovable_clone.dto.project;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) for project summary information.
 * Represents a project with its ID, name, and timestamps.
 *
 * @param id        the unique identifier of the project
 * @param name      the name of the project
 * @param createdAt the timestamp when the project was created
 * @param updatedAt the timestamp when the project was last updated
 */
public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
