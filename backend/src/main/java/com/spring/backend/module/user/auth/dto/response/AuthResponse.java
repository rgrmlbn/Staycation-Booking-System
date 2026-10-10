package com.spring.backend.module.user.auth.dto.response;

import com.spring.backend.module.user.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private UserRole role;

}
