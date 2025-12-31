package com.anee.projects.lovable_clone.dto.auth;

/**
 * Request payload for user login.
 *
 * @param email    User's email address
 * @param password User's password
 */
public record LoginRequest(
        String email,
        String password
) {
}
