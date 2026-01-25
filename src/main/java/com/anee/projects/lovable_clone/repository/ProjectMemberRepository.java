package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.ProjectMember;
import com.anee.projects.lovable_clone.entities.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <h6>Repository interface for managing ProjectMember entities.</h6>
 * Provides methods to interact with the database for project member data.
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    /**
     * Retrieves a list of project members by the project ID.
     *
     * @param projectId the ID of the project
     * @return a list of ProjectMember entities
     */
    List<ProjectMember> findByIdProjectId(Long projectId);
}
