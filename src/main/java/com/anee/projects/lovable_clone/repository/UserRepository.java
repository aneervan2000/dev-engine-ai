package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// No implementation needed; Spring Data JPA provides it automatically SimpleJpaRepository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
