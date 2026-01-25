package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <h6>Repository interface for managing Project entities.</h6>
 * Provides methods to interact with the database for project data.
 */

// No implementation needed; Spring Data JPA provides it automatically SimpleJpaRepository
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Retrieves all projects accessible by a specific user,
     * includes only projects that are not soft-deleted.
     *
     * @param userId the ID of the user
     * @return a list of accessible Project entities
     */
    @Query("""
            SELECT p FROM Project p
            WHERE p.deletedAt IS NULL
            AND EXISTS (
                SELECT 1 FROM ProjectMember pm
                WHERE pm.id.userId = :userId
                AND pm.id.projectId = p.id
            )
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllAccessibleByUserId(@Param("userId") Long userId);

    /**
     * Retrieves the accessible project by its ID and the user's ID.
     * Includes only projects that are not soft-deleted.
     *
     * @param projectId the ID of the project
     * @param userId    the ID of the user
     * @return an Optional containing the accessible Project entity
     */
    @Query("""
            SELECT p FROM Project p
            WHERE p.id = :projectId
                AND p.deletedAt IS NULL
                AND EXISTS (
                    SELECT 1 FROM ProjectMember pm
                    WHERE pm.id.userId = :userId
                    AND pm.id.projectId = :projectId
                )
            """)
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
