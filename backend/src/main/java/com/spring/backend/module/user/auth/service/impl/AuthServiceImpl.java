package com.spring.backend.module.user.auth.service.impl;


import com.spring.backend.exception.user.user.DuplicateEmailException;
import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RefreshTokenRequest;
import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.interfaces.AuthService;
import com.spring.backend.module.user.auth.service.interfaces.LoginRateLimiterService;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.security.principal.UserPrincipal;
import com.spring.backend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginRateLimiterService loginRateLimiterService;

    // Register a new user, checking for duplicate emails and securely encoding the password
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // Check whether the email is already registered
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        UserEntity user = userMapper.toEntity(request);

        // Encode the password before storing the user
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return userMapper.toRegisterResponse(user);
    }

    // Authenticate a user and generate access and refresh tokens
    @Override
    public AuthResponse login(LoginRequest request) {

        // Check login rate limits before attempting authentication
        loginRateLimiterService.checkLimits(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Retrieve the authenticated user principal after authentication succeeds
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Generate a short-lived access token and a refresh token for the authenticated user
        String accessToken = jwtUtil.generateAccessToken(principal);
        String refreshToken = refreshTokenService.createRefreshToken(principal.getUser());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Validate and rotate a refresh token, then generate a new access token
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        // Validate the existing refresh token before using it
        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        UserPrincipal principal = new UserPrincipal(oldToken.getUser());

        // Rotate the refresh token and generate a new access token
        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        String newAccessToken = jwtUtil.generateAccessToken(principal);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // Log out the current user by revoking refresh tokens and blacklisting the access token
    @Override
    @Transactional
    public void logout() {

        // Retrieve the JWT stored in the authentication credentials by the JwtFilter
        String accessToken = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        // Retrieve the authenticated user's email from the security context
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Load the user so all of their refresh tokens can be revoked
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        // Revoke all refresh tokens and blacklist the current access token
        refreshTokenService.deleteAllByUser(user);
        tokenBlacklistService.blacklist(accessToken);

        // Clear the authenticated user's security context
        SecurityContextHolder.clearContext();
    }
}