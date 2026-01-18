package com.anee.projects.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for user signup.
 *
 * @param email    User's email address
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
