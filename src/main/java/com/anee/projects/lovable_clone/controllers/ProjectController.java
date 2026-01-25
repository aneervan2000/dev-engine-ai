package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.project.ProjectRequest;
import com.anee.projects.lovable_clone.dto.project.ProjectResponse;
import com.anee.projects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.anee.projects.lovable_clone.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h6>REST controller for managing projects.</h6>
 * Provides endpoints for creating, retrieving, updating, and deleting projects.
 * Delegates business logic to the ProjectService.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Retrieves all projects accessible by the current user.
     *
     * @return a ResponseEntity containing a list of ProjectSummaryResponse
     */

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    /**
     * Retrieves a specific project by its ID for the current user.
     *
     * @param id the ID of the project
     * @return a ResponseEntity containing the ProjectResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getUserProjectById(id));
    }

    /**
     * Creates a new project for the current user.
     *
     * @param request the ProjectRequest containing project details and validation
     * @return a ResponseEntity containing the created ProjectResponse
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    /**
     * Updates an existing project for the current user.
     *
     * @param id the ID of the project to update
     * @param request the ProjectRequest containing updated project details and validation
     * @return a ResponseEntity containing the updated ProjectResponse
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody @Valid ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    /**
     * Soft deletes a project for the current user.
     *
     * @param id the ID of the project to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
