package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.auth.AuthResponse;
import com.anee.projects.lovable_clone.dto.auth.LoginRequest;
import com.anee.projects.lovable_clone.dto.auth.SignupRequest;
import com.anee.projects.lovable_clone.dto.auth.UserProfileResponse;
import com.anee.projects.lovable_clone.service.AuthService;
import com.anee.projects.lovable_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related endpoints such as signup, login, and fetching the current user's profile.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Handles user signup requests.
     *
     * @param request the signup request containing email, name, and password
     * @return the authentication response with token and user profile
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * Handles user login requests.
     *
     * @param request the login request containing email and password
     * @return the authentication response with token and user profile
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return the user profile response
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = 1L;
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
