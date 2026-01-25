package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.member.InviteMemberRequest;
import com.anee.projects.lovable_clone.dto.member.MemberResponse;
import com.anee.projects.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.anee.projects.lovable_clone.entities.Project;
import com.anee.projects.lovable_clone.entities.ProjectMember;
import com.anee.projects.lovable_clone.entities.ProjectMemberId;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.mapper.ProjectMemberMapper;
import com.anee.projects.lovable_clone.repository.ProjectMemberRepository;
import com.anee.projects.lovable_clone.repository.ProjectRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * <h6>Service implementation for managing project members.</h6>
 * Contains the business logic for retrieving, inviting, updating, and removing members of a project.
 *
 * <p>
 * Dependencies:
 * <li>ProjectMemberRepository: Repository for accessing project member data.</li>
 * <li>ProjectRepository: Repository for accessing project data.</li>
 * <li>ProjectMemberMapper: Mapper for converting entities to DTOs.</li>
 * <li>UserRepository: Repository for accessing user data.</li>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    /**
     * Retrieves the list of members for a specific project.
     * Includes the project owner and all other members.
     *
     * @param projectId the ID of the project
     * @return a list of MemberResponse objects representing the project members
     */
    @Override
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        return projectMemberRepository.findByIdProjectId(projectId)
                        .stream()
                        .map(projectMemberMapper::toProjectMemberResponseFromMember)
                        .toList();
    }

    /**
     * Invites a new member to the project.
     * Validates the inviter's permissions and ensures the invitee is not already a member.
     *
     * @param projectId the ID of the project
     * @param request   the invitation details (email and role)
     * @return a MemberResponse object representing the invited member
     */
    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        User invitee = userRepository.findByUsername(request.username()).orElseThrow();

        if (invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("User is already a member");
        }

        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    /**
     * Updates the role of an existing project member.
     * Validates the updater's permissions and updates the member's role.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member
     * @param request   the new role details
     * @return a MemberResponse object representing the updated member
     */
    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    /**
     * Removes a member from the project.
     * Validates the remover's permissions and ensures the member exists before removal.
     *
     * @param projectId the ID of the project
     * @param memberId  the ID of the member to be removed
     */
    @Override
    public void removeProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member not found");
        }
        projectMemberRepository.deleteById(projectMemberId);
    }

    /**
     * Retrieves an accessible project by its ID and the user's ID.
     * Ensures the user has access to the project.
     *
     * @param projectId the ID of the project
     * @param userId    the ID of the user making the request
     * @return the accessible Project entity
     */
    // INTERNAL METHODS
    private Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }
}
