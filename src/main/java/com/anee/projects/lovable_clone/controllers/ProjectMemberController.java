package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.member.InviteMemberRequest;
import com.anee.projects.lovable_clone.dto.member.MemberResponse;
import com.anee.projects.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.anee.projects.lovable_clone.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h6>REST controller for managing project members.</h6>
 * Provides endpoints to retrieve, invite, update, and remove members of a project.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    /**
     * Retrieves the list of members for a specific project.
     *
     * @param projectId the ID of the project
     * @return a ResponseEntity containing the list of project members
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    /**
     * Invites a new member to the project.
     *
     * @param projectId the ID of the project
     * @param request   the invitation details (email and role) and validation
     * @return a ResponseEntity containing the invited member's details
     */
    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember (@PathVariable Long projectId, @RequestBody @Valid InviteMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.inviteMember(projectId, request));
    }

    /**
     * Updates the role of an existing project member.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member
     * @param request   the new role details and validation
     * @return a ResponseEntity containing the updated member's details
     */
    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId,
                                                           @PathVariable Long memberId,
                                                           @RequestBody @Valid UpdateMemberRoleRequest request) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request));
    }

    /**
     * Removes a member from the project.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member to be removed
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId,
                                                           @PathVariable Long memberId) {
        projectMemberService.removeProjectMember(projectId, memberId);
        return ResponseEntity.noContent().build();

    }

}
