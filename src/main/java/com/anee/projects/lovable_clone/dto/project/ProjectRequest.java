package com.anee.projects.lovable_clone.dto.project;

import jakarta.validation.constraints.NotBlank;

/**
 * <h6>Data Transfer Object (DTO) for creating or updating a project.</h6>
 * @param name : The name of the project which should not be blank.
 */

public record ProjectRequest(

        @NotBlank
        String name
) {
}
