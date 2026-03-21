package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.auth.AuthResponse;
import com.anee.projects.lovable_clone.dto.auth.LoginRequest;
import com.anee.projects.lovable_clone.dto.auth.SignupRequest;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.error.BadRequestException;
import com.anee.projects.lovable_clone.mapper.UserMapper;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <h6>Service implementation for handling authentication-related operations.</h6>
 * <p>
 * This class provides business logic for user registration and login processes.
 * It ensures unique usernames during signup, encodes user passwords securely,
 * and maps between DTOs and entity objects.
 * </p>
 *
 * <p>
 * <b>Key Responsibilities:</b>
 * <ul>
 *   <li>Registers new users with unique usernames and encoded passwords.</li>
 *   <li>Handles user login (to be implemented).</li>
 *   <li>Throws {@link BadRequestException} if a username already exists during signup.</li>
 *   <li>Maps between request DTOs and {@link User} entities using {@link UserMapper}.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This service is thread-safe due to Spring's default singleton bean scope and use of final dependencies.
 * </p>
 *
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system.
     * <p>
     * Checks for existing username, encodes the password, saves the user,
     * and returns an authentication response.
     * </p>
     *
     * @param request the signup request containing user registration details
     * @return an {@link AuthResponse} containing a JWT token and user profile
     * @throws BadRequestException if the username already exists
     */
    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username: " + request.username());
        });

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        // Generate JWT token
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    /**
     * Authenticates a user and returns an authentication response.
     *
     * @param request the login request containing user credentials
     * @return an {@link AuthResponse} upon successful authentication
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()
                )
        );

        // Retrieve authenticated user details
        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
