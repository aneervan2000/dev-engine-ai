package com.anee.projects.lovable_clone.dto.auth;

/**
 * Response containing the user's profile information.
 *
 * @param id        Unique identifier of the user
 * @param email     User's email address
 * @param name      User's full name
 * @param avatarUrl URL to the user's avatar image
 */
public record UserProfileResponse(
        Long id,
        String email,
        String name,
        String avatarUrl
) {
}
