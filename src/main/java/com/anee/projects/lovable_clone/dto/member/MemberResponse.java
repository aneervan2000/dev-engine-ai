package com.anee.projects.lovable_clone.dto.member;

import com.anee.projects.lovable_clone.enums.ProjectRole;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a project member.
 * Contains details about the member, including their user ID, email, name, role, and invitation timestamp.
 *
 * @param userId     the unique identifier of the user
 * @param email      the email address of the user
 * @param name       the name of the user
 * @param projectRole the role of the user in the project
 * @param invitedAt  the timestamp when the user was invited to the project
 */
public record MemberResponse(
        Long userId,
        String email,
        String name,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
