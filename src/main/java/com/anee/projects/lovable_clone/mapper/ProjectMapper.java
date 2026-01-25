package com.anee.projects.lovable_clone.mapper;

import com.anee.projects.lovable_clone.dto.project.ProjectResponse;
import com.anee.projects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.anee.projects.lovable_clone.entities.Project;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <h6>Mapper interface for converting Project entities to their corresponding DTOs.</h6>
 *
 * Utilizes MapStruct to generate the implementation at compile time.
 * This mapper is used to transform Project entities into ProjectResponse and ProjectSummaryResponse objects.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    /**
     * Converts a Project entity to a ProjectResponse DTO.
     *
     * @param project the Project entity to be converted
     * @return the corresponding ProjectResponse DTO
     */
    ProjectResponse toProjectResponse(Project project);

    /**
     * Converts a Project entity to a ProjectSummaryResponse DTO.
     *
     * @param project the Project entity to be converted
     * @return the corresponding ProjectSummaryResponse DTO
     */
    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    /**
     * Converts a list of Project entities to a list of ProjectSummaryResponse DTOs.
     *
     * @param projects the list of Project entities to be converted
     * @return the corresponding list of ProjectSummaryResponse DTOs
     */
    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects);
}