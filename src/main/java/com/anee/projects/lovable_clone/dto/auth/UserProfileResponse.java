package com.anee.projects.lovable_clone.dto.auth;

/**
 * Response containing the user's profile information.
 *
 * @param id        Unique identifier of the user
 * @param username     User's email address
 * @param name      User's full name
 */
public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
