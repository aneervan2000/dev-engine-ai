package com.anee.projects.lovable_clone.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * <h6>Represents the authenticated user's principal information stored in the JWT.</h6>
 *
 * @param userId     Unique identifier of the user
 * @param username   Username (email) of the user
 * @param authorities List of granted authorities/roles assigned to the user
 */
public record JwtUserPrincipal (
        Long userId,
        String username,
        List<GrantedAuthority> authorities
){
}
