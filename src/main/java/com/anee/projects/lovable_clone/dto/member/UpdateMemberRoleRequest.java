package com.anee.projects.lovable_clone.dto.member;

import com.anee.projects.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for updating a project member's role.
 *
 * @param role the new role to be assigned to the member
 */
public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
