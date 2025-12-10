package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.project.ProjectRequest;
import com.anee.projects.lovable_clone.dto.project.ProjectResponse;
import com.anee.projects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.anee.projects.lovable_clone.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects() {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.ok(projectService.getUserProjectById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequest request) {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.ok(projectService.updateProject(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        projectService.softDelete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
