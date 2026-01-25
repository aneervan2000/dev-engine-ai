package com.anee.projects.lovable_clone.mapper;

import com.anee.projects.lovable_clone.dto.auth.SignupRequest;
import com.anee.projects.lovable_clone.dto.auth.UserProfileResponse;
import com.anee.projects.lovable_clone.entities.User;
import org.mapstruct.Mapper;

/**
 * <h6>Mapper interface for converting User entities to their corresponding DTOs and vice versa.</h6>
 *
 * Utilizes MapStruct to generate the implementation at compile time.
 * This mapper is used to transform SignupRequest DTOs into User entities
 * and User entities into UserProfileResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a SignupRequest DTO to a User entity.
     * @param request the SignupRequest DTO containing user signup information
     * @return the corresponding User entity
     */
    User toEntity(SignupRequest request);

    /**
     * Converts a User entity to a UserProfileResponse DTO.
     *
     * @param user the User entity to be converted
     * @return the corresponding UserProfileResponse DTO
     */
    UserProfileResponse toUserProfileResponse(User user);
}
