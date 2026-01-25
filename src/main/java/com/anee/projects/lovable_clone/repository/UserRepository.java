package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <h6>Repository interface for managing User entities.</h6>
 * Provides methods to interact with the database for user data.
 */
// No implementation needed; Spring Data JPA provides it automatically SimpleJpaRepository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user
     * @return an Optional containing the User entity
     */
    Optional<User> findByUsername(String username);
}
