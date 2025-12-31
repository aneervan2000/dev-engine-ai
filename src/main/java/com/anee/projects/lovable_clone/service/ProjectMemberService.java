package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.member.InviteMemberRequest;
import com.anee.projects.lovable_clone.dto.member.MemberResponse;
import com.anee.projects.lovable_clone.dto.member.UpdateMemberRoleRequest;

import java.util.List;

/**
 * Service interface for managing project members.
 * Provides methods to retrieve, invite, update, and remove members of a project.
 */
public interface ProjectMemberService {

    /**
     * Retrieves the list of members for a specific project.
     *
     * @param projectId the ID of the project
     * @param userId    the ID of the user making the request
     * @return a list of MemberResponse objects representing the project members
     */
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    /**
     * Invites a new member to the project.
     *
     * @param projectId the ID of the project
     * @param request   the invitation details (email and role)
     * @param userId    the ID of the user making the request
     * @return a MemberResponse object representing the invited member
     */
    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    /**
     * Updates the role of an existing project member.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member
     * @param request   the new role details
     * @param userId    the ID of the user making the request
     * @return a MemberResponse object representing the updated member
     */
    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId);

    /**
     * Removes a member from the project.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member to be removed
     * @param userId    the ID of the user making the request
     */
    void removeProjectMember(Long projectId, Long memberId, Long userId);
}
