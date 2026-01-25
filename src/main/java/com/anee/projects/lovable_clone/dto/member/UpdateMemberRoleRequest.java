package com.anee.projects.lovable_clone.dto.member;

import com.anee.projects.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

/**
 * <h6>Data Transfer Object (DTO) for updating a project member's role.</h6
 *
 * @param role the new role to be assigned to the member which is not null
 */
public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
