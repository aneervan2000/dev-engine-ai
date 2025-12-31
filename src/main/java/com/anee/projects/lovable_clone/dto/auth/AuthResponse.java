package com.anee.projects.lovable_clone.dto.auth;

/**
 * Response returned after successful authentication (login or signup).
 * Contains the JWT token and the authenticated user's profile.
 *
 * @param token JWT token for authenticated requests
 * @param user  Profile information of the authenticated user
 */
public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
