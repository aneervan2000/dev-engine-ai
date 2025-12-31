package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.auth.AuthResponse;
import com.anee.projects.lovable_clone.dto.auth.LoginRequest;
import com.anee.projects.lovable_clone.dto.auth.SignupRequest;

/**
 * Service interface for handling authentication-related operations.
 * Provides methods for user signup and login, returning authentication responses.
 */
public interface AuthService {

    /**
     * Handles user signup by processing the provided signup request.
     *
     * @param request The signup request containing user details such as email, name, and password.
     * @return An AuthResponse containing the JWT token and the authenticated user's profile.
     */
    AuthResponse signup(SignupRequest request);

    /**
     * Handles user login by processing the provided login request.
     *
     * @param request The login request containing the user's email and password.
     * @return An AuthResponse containing the JWT token and the authenticated user's profile.
     */
    AuthResponse login(LoginRequest request);
}
