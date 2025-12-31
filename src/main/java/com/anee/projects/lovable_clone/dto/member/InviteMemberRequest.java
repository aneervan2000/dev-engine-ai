package com.anee.projects.lovable_clone.dto.member;

import com.anee.projects.lovable_clone.enums.ProjectRole;

/**
 * Data Transfer Object (DTO) for inviting a new member to a project.
 *
 * @param email the email address of the user to be invited
 * @param role  the role to be assigned to the invited user
 */
public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
