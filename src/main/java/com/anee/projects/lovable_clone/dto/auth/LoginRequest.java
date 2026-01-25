package com.anee.projects.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * <h6>Request payload for user login.</h6>
 *
 * @param username    User's email address which must be a valid email format and not blank
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
