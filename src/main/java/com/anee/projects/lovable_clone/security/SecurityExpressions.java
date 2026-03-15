package com.anee.projects.lovable_clone.security;



import com.anee.projects.lovable_clone.enums.ProjectPerminssion;
import com.anee.projects.lovable_clone.enums.ProjectRole;
import com.anee.projects.lovable_clone.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Contains all the security expressions used in method security annotations

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    /**
     * Checks if the current authenticated user has the required permission for the specified project.
     * @param projectId
     * @param requiredPermission
     * @return boolean if the user has the required permission in the project else false
     */
    private boolean hasPermission(Long projectId, ProjectPerminssion requiredPermission) {
        // get current authenticated user's id
        Long userId = authUtil.getCurrentUserId();

        /**
         * This method queries the ProjectMemberRepository to find the role of the user in the specified project.
         * It then checks if the permissions associated with that role include the required permission.
         * If the user has a role in the project, it returns true if the role's permissions contain the required permission, otherwise it returns false.
         * If the user does not have a role in the project (i.e., they are not a member), it returns false by default.
         */
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(projectRole -> projectRole.getPermissions().contains(requiredPermission))  // converts role to boolean
                .orElse(false);
    }
    /**
     * Checks if the current authenticated user can view the specified project.
     *
     * @param projectId
     * @return boolean if the user has a role in the project (OWNER, EDITOR, VIEWER) else false
     */
    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, ProjectPerminssion.VIEW);
    }

    /**
     * Checks if the current authenticated user can edit the specified project.
     *
     * @param projectId
     * @return boolean if the user has a role in the project (OWNER, EDITOR) else false
     */
    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, ProjectPerminssion.EDIT);
    }

    /**
     * Checks if the current authenticated user can delete the specified project.
     *
     * @param projectId
     * @return boolean if the user has a role in the project (OWNER) else false
     */
    public boolean canDeleteProject(Long projectId) {
        return hasPermission(projectId, ProjectPerminssion.DELETE);
    }

    /**
     * Checks if the current authenticated user can view the members of the specified project.
     * @param projectId
     * @return boolean if the user has a role in the project (OWNER) else false
     */
    public boolean canViewMembers(Long projectId) {
        return hasPermission(projectId, ProjectPerminssion.VIEW_MEMBERS);
    }

    /**
     * Checks if the current authenticated user can manage the members of the specified project.
     * @param projectId
     * @return boolean if the user has a role in the project (OWNER) else false
     */
    public boolean canManageMembers(Long projectId) {
        return hasPermission(projectId, ProjectPerminssion.MANAGE_MEMBERS);
    }
}
