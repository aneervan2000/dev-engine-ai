package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.project.ProjectRequest;
import com.anee.projects.lovable_clone.dto.project.ProjectResponse;
import com.anee.projects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.anee.projects.lovable_clone.entities.Project;
import com.anee.projects.lovable_clone.entities.ProjectMember;
import com.anee.projects.lovable_clone.entities.ProjectMemberId;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.enums.ProjectRole;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.mapper.ProjectMapper;
import com.anee.projects.lovable_clone.repository.ProjectMemberRepository;
import com.anee.projects.lovable_clone.repository.ProjectRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Implementation of the ProjectService interface.
 * Provides the business logic for managing projects.
 * Uses repositories and mappers to interact with the database and convert entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * {@inheritDoc}
     */
    // Logic to create a new project
    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        // Project is created
        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        // When a project is created, the owner is added as a member with OWNER role
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    /**
     * {@inheritDoc}
     */
    // Logic to get all projects for a user along with the project in which the user is member.
    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        List<Project> projects = projectRepository.findAllccessibleByUserId(userId);
        return projectMapper.toListOfProjectSummaryResponse(projects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
        Project project = getAccessibleProjectById(id, userId);
        return projectMapper.toProjectResponse(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = getAccessibleProjectById(id, userId);

        project.setName(request.name());
        project = projectRepository.save(project); // optional, as within a transaction, changes are auto-detected

        return projectMapper.toProjectResponse(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void softDelete(Long id, Long userId) {
        Project project = getAccessibleProjectById(id, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }

    /**
     * Retrieves a project accessible by a specific user.
     *
     * @param projectId the ID of the project
     * @param userId the ID of the user
     * @return the Project entity
     */
    // INTERNAL METHODS
    private Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project ", projectId.toString()));
    }
}
