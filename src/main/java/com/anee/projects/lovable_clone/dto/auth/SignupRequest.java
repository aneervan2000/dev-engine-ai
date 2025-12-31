package com.anee.projects.lovable_clone.dto.auth;

/**
 * Request payload for user signup.
 *
 * @param email    User's email address
 * @param name     User's full name
 * @param password User's password
 */
public record SignupRequest(
        String email,
        String name,
        String password
) {
}
