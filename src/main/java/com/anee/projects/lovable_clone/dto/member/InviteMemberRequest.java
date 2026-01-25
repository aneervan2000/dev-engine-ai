package com.anee.projects.lovable_clone.dto.member;

import com.anee.projects.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <h6>Data Transfer Object (DTO) for inviting a new member to a project.</h6>
 *
 * @param username the email address of the user to be invited
 * @param role  the role to be assigned to the invited user
 */
public record InviteMemberRequest(

        @Email
        @NotBlank
        String username,

        @NotNull
        ProjectRole role
) {
}
