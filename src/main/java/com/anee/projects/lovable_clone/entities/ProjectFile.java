package com.anee.projects.lovable_clone.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * <h2>Represents files within a project.</h2>
 * <i>The ProjectFile entity models files that is part of a project workspace:</i>
 * <hr>
 * <ul>
 *   <li>Each file belongs to a single {@link Project} (via {@code projectId}).</li>
 *   <li>Each file has a unique path within its project (via {@code path}). </li>
 *   <li>Files are stored in an object storage system (e.g., MinIO) and referenced by {@code minioObjectKey}.</li>
 *   <li>Tracks which user created and last updated the file (via {@link User}).</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key, unique identifier for the file.</li>
 *   <li>{@code project}: Foreign key referencing the owning {@link Project}.</li>
 *   <li>{@code path}: Unique path of the file within the project.</li>
 *   <li>{@code minioObjectKey}: Key for locating the file in object storage.</li>
 *   <li>{@code createdBy}: User who created the file.</li>
 *   <li>{@code updatedBy}: User who last updated the file.</li>
 *   <li>{@code createdAt}: Timestamp when the file was created.</li>
 *   <li>{@code updatedAt}: Timestamp when the file was last updated.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>Many-to-one with {@link Project} (each file belongs to one project).</li>
 *   <li>Many-to-one with {@link User} (createdBy, updatedBy).</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile {

    Long id;

    Project project;

    String path;

    String minioObjectKey;

    Instant createdAt;
    Instant updatedAt;

    User createdBy;
    User updatedBy;
}
