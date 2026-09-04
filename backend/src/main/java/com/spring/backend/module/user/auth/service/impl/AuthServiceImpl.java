package com.spring.backend.module.user.auth.service.impl;


import com.spring.backend.exception.DuplicateEmailException;
import com.spring.backend.exception.ResourceNotFoundException;
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

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return userMapper.toRegisterResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        loginRateLimiterService.checkLimits(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // After authentication passes, load the principal
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(principal);           // ✅ UserDetails
        String refreshToken = refreshTokenService.createRefreshToken(principal.getUser()); // ✅ UserEntity

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        UserPrincipal principal = new UserPrincipal(oldToken.getUser());

        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        String newAccessToken = jwtUtil.generateAccessToken(principal);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    @Override
    @Transactional
    public void logout() {
        String accessToken = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials(); // 👈 reads the JWT stored by JwtFilter

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        refreshTokenService.deleteAllByUser(user);
        tokenBlacklistService.blacklist(accessToken);
        SecurityContextHolder.clearContext();
    }
}
