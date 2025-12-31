package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.project.ProjectRequest;
import com.anee.projects.lovable_clone.dto.project.ProjectResponse;
import com.anee.projects.lovable_clone.dto.project.ProjectSummaryResponse;

import java.util.List;

/**
 * Service interface for managing projects.
 * Defines the contract for project-related operations.
 */
public interface ProjectService {

    /**
     * Retrieves all projects accessible by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of ProjectSummaryResponse
     */
    List<ProjectSummaryResponse> getUserProjects(Long userId);

    /**
     * Retrieves a specific project by its ID for a specific user.
     *
     * @param id the ID of the project
     * @param userId the ID of the user
     * @return the ProjectResponse
     */
    ProjectResponse getUserProjectById(Long id, Long userId);

    /**
     * Creates a new project for a specific user.
     *
     * @param request the ProjectRequest containing project details
     * @param userId the ID of the user
     * @return the created ProjectResponse
     */
    ProjectResponse createProject(ProjectRequest request, Long userId);

    /**
     * Updates an existing project for a specific user.
     *
     * @param id the ID of the project to update
     * @param request the ProjectRequest containing updated project details
     * @param userId the ID of the user
     * @return the updated ProjectResponse
     */
    ProjectResponse updateProject(Long id, ProjectRequest request, Long userId);

    /**
     * Soft deletes a project for a specific user.
     *
     * @param id the ID of the project to delete
     * @param userId the ID of the user
     */
    void softDelete(Long id, Long userId);
}
