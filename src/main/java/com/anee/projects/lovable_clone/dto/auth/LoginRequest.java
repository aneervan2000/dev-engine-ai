package com.anee.projects.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for user login.
 *
 * @param email    User's email address
 * @param password User's password
 */
public record LoginRequest(

        @Email
        @NotBlank
        String username,

        @Size(min = 4, max = 50)
        String password
) {
}
