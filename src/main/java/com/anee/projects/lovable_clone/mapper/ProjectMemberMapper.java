package com.anee.projects.lovable_clone.mapper;

import com.anee.projects.lovable_clone.dto.member.MemberResponse;
import com.anee.projects.lovable_clone.entities.ProjectMember;
import com.anee.projects.lovable_clone.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * <h6>Mapper interface for converting ProjectMember and User entities to MemberResponse DTOs.</h6>
 * Utilizes MapStruct to generate the implementation at compile time.
 */
@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    /**
     * Converts a User entity (project owner) to a MemberResponse DTO.
     *
     * @param owner the User entity representing the project owner
     * @return the corresponding MemberResponse DTO
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    /**
     * Converts a ProjectMember entity to a MemberResponse DTO.
     *
     * @param projectMember the ProjectMember entity
     * @return the corresponding MemberResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "name", source = "user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);
}
