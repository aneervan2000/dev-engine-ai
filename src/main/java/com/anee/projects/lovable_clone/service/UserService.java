package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {

    UserProfileResponse getProfile(Long userId);
}
