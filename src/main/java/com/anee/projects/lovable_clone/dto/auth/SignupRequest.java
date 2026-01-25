package com.anee.projects.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * <h6>Request payload for user signup.</h6>
 *
 * @param username    User's email address which must be a valid email format and not blank
 * @param name     User's full name
 * @param password User's password
 */
public record SignupRequest(

        @Email
        @NotBlank
        String username,

        @Size(min = 1, max = 30)
        String name,

        @Size(min = 4)
        String password
) {
}
