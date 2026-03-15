package com.anee.projects.lovable_clone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <h2>ProjectPerminssion Enum</h2>
 * <p>
 * Represents the set of permissions that can be assigned to a project or its members.
 * </p>
 * <ul>
 *   <li><b>VIEW</b>: Permission to view the project.</li>
 *   <li><b>EDIT</b>: Permission to edit the project.</li>
 *   <li><b>DELETE</b>: Permission to delete the project.</li>
 *   <li><b>MANAGE_MEMBERS</b>: Permission to manage project members.</li>
 *   <li><b>VIEW_MEMBERS</b>: Permission to view project members.</li>
 * </ul>
 * <p>
 * Each permission is associated with a string value used for authorization checks.
 * </p>
 */
@RequiredArgsConstructor
@Getter
public enum ProjectPerminssion {
    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),

    MANAGE_MEMBERS("project_members:manage"),
    VIEW_MEMBERS("project_members:view");

    private final String value;
}
