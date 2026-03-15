package com.anee.projects.lovable_clone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.anee.projects.lovable_clone.enums.ProjectPerminssion.*;

/**
 * <h6>Enum representing the different roles a user can have in a project.</h6>
 * <ul>
 * <li>EDITOR: Can edit project content.</li>
 * <li>VIEWER: Can view project content.</li>
 * <li>OWNER: Has full control over the project.</li>
 * </ul>
 *
 * Permissions are defined for each role, and the enum provides a method to check if a role has a specific permission.<br>
 * This design allows for flexible role-based access control in the application.<br>
 * Each role is associated with a set of permissions that determine what actions users with that role can perform on the project.
 */

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(VIEW, EDIT, DELETE, VIEW_MEMBERS),
    VIEWER(Set.of(VIEW, VIEW_MEMBERS)),
    OWNER(Set.of(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS));


    /**
     * Constructor for ProjectRole enum.
     * Accepts a variable number of ProjectPermissions arguments and initializes the permissions set for the role.
     * The permissions are stored in a Set to ensure uniqueness and efficient lookup.
     *
     * @param permissions
     */
    ProjectRole(ProjectPerminssion... permissions) {
        this.permissions = Set.of(permissions);
    }

    // Set of permissions associated with the role
    private final Set<ProjectPerminssion> permissions;
}
