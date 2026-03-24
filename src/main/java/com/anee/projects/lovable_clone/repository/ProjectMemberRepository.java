package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.ProjectMember;
import com.anee.projects.lovable_clone.entities.ProjectMemberId;
import com.anee.projects.lovable_clone.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Finds the role of a user in a specific project.
     *
     * @param projectId the ID of the project
     * @param userId    the ID of the user
     * @return an Optional containing the ProjectRole if found, otherwise empty
     */
    @Query("SELECT pm.projectRole FROM ProjectMember pm WHERE pm.id.projectId = :projectId AND pm.id.userId = :userId")
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query("SELECT COUNT(pm) FROM ProjectMember pm WHERE pm.id.userId = :userId AND pm.projectRole = 'OWNER'")
    int countProjectOwnedByUser(@Param("userId") Long userId);
}
