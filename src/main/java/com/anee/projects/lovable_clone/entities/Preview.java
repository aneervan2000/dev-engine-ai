package com.anee.projects.lovable_clone.entities;

import com.anee.projects.lovable_clone.enums.PreviewStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * <h2>Represents an active preview session for a project.</h2>
 * <i>The Preview entity tracks the lifecycle and metadata of a live preview environment
 * (such as a running container or pod) associated with a project.</i>
 * <hr>
 * <ul>
 *   <li>Each preview is uniquely associated with a single {@link Project} (via {@code projectId}).</li>
 *   <li>Each project can have at most one active preview at a time.</li>
 * </ul>
 *
 * <h6>Fields:</h6>
 * <ol>
 *   <li>{@code id}: Primary key, unique identifier for the preview session.</li>
 *   <li>{@code project}: Reference to the associated {@link Project} (unique, not null).</li>
 *   <li>{@code namespace}: The Kubernetes or orchestration namespace for the preview environment.</li>
 *   <li>{@code podName}: The name of the pod or container running the preview.</li>
 *   <li>{@code previewUrl}: The URL endpoint where the preview can be accessed.</li>
 *   <li>{@code status}: Current status of the preview (e.g. CREATED, PROCESSING, READY, FAILED) linked to enum {@link PreviewStatus}.</li>
 *   <li>{@code startedAt}: Timestamp when the preview session started.</li>
 *   <li>{@code terminatedAt}: Timestamp when the preview session ended (null if still active).</li>
 *   <li>{@code createdAt}: Timestamp when the preview record was created.</li>
 * </ol>
 *
 * <h6>Relationships:</h6>
 * <ul>
 *   <li>One-to-one with {@link Project} (each project can have one active preview).</li>
 * </ul>
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {

    Long id;

    Project project;

    String namespace;
    String podName;
    String previewUrl;

    PreviewStatus status;

    Instant startedAt;
    Instant terminatedAt;

    Instant createdAt;
}
