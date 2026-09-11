package com.spring.backend.module.user.auth.service.impl;

import com.spring.backend.exception.user.user.DuplicateEmailException;
import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RefreshTokenRequest;
import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.interfaces.LoginRateLimiterService;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.security.principal.UserPrincipal;
import com.spring.backend.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository; // Fake repository — no real database involved

    @Mock
    private UserMapper userMapper; // Fake mapper — controls exactly what DTO/entity conversion returns

    @Mock
    private PasswordEncoder passwordEncoder; // Fake encoder — controls the "hashed" password directly

    @Mock
    private AuthenticationManager authenticationManager; // Fake auth manager — controls whether credentials "pass"

    @Mock
    private JwtUtil jwtUtil; // Fake JWT generator — controls the token strings returned

    @Mock
    private RefreshTokenService refreshTokenService; // Fake refresh-token store — no real persistence involved

    @Mock
    private TokenBlacklistService tokenBlacklistService; // Fake blacklist — confirms it's invoked without needing real storage

    @Mock
    private LoginRateLimiterService loginRateLimiterService; // Fake rate limiter — confirms it's invoked without enforcing real limits

    @InjectMocks
    private AuthServiceImpl authService; // Real service, wired with all mocks above

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("roger@example.com");
        user.setPassword("encoded-old-password");
    }

    // ---------- register() ----------

    @Test
    @DisplayName("Should map, save, and return the registered user when the email is available")
    void register_emailAvailable_savesAndReturnsResponse() {
        RegisterRequest request = mock(RegisterRequest.class);
        UserEntity mappedEntity = new UserEntity(); // What the mapper produces from the request
        RegisterResponse response = mock(RegisterResponse.class);

        when(request.getEmail()).thenReturn("new@example.com");
        when(request.getPassword()).thenReturn("raw-password");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false); // Simulate the email being available
        when(userMapper.toEntity(request)).thenReturn(mappedEntity); // Simulate converting the request DTO into an entity
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password"); // Simulate hashing the password
        when(userMapper.toRegisterResponse(mappedEntity)).thenReturn(response);

        RegisterResponse result = authService.register(request);

        assertThat(mappedEntity.getPassword()).isEqualTo("encoded-password"); // Confirm the raw password was replaced with the hash
        assertThat(result).isEqualTo(response);
        verify(userRepository).save(mappedEntity); // Confirm the mapped entity was actually persisted
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when the email is already registered")
    void register_emailAlreadyTaken_throws() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.getEmail()).thenReturn("taken@example.com");
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true); // Simulate another user already owning this email

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any()); // Confirm nothing was saved once the conflict was detected
    }

    // ---------- login() ----------

    @Test
    @DisplayName("Should authenticate the user and return access and refresh tokens")
    void login_validCredentials_returnsTokens() {
        LoginRequest request = mock(LoginRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);

        when(request.getEmail()).thenReturn("roger@example.com");
        when(request.getPassword()).thenReturn("raw-password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication); // Simulate credentials passing authentication
        when(authentication.getPrincipal()).thenReturn(principal);
        when(principal.getUser()).thenReturn(user);
        when(jwtUtil.generateAccessToken(principal)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse result = authService.login(request);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        verify(loginRateLimiterService).checkLimits("roger@example.com"); // Confirm the rate limiter was consulted before authenticating
    }

    // ---------- refreshToken() ----------

    @Test
    @DisplayName("Should validate the old token and return newly rotated tokens")
    void refreshToken_validToken_returnsNewTokens() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken oldToken = mock(RefreshToken.class);

        when(request.getRefreshToken()).thenReturn("old-refresh-token");
        when(refreshTokenService.validateRefreshToken("old-refresh-token")).thenReturn(oldToken); // Simulate the old token being valid
        when(oldToken.getUser()).thenReturn(user);
        when(refreshTokenService.rotateRefreshToken(oldToken)).thenReturn("new-refresh-token");
        when(jwtUtil.generateAccessToken(any(UserPrincipal.class))).thenReturn("new-access-token");

        AuthResponse result = authService.refreshToken(request);

        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenService).rotateRefreshToken(oldToken); // Confirm the old token was actually rotated, not reused
    }

    // ---------- logout() ----------

    @Test
    @DisplayName("Should delete refresh tokens, blacklist the access token, and clear the security context")
    void logout_validSession_cleansUpTokensAndContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getCredentials()).thenReturn("current-access-token"); // Simulate the JWT stored by JwtFilter
        when(authentication.getName()).thenReturn("roger@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("roger@example.com")).thenReturn(Optional.of(user)); // Simulate finding the logged-in user

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            authService.logout();

            verify(refreshTokenService).deleteAllByUser(user); // Confirm the user's sessions were invalidated
            verify(tokenBlacklistService).blacklist("current-access-token"); // Confirm the current access token was blacklisted
            mockedContext.verify(SecurityContextHolder::clearContext); // Confirm the security context was cleared afterward
        }
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when the authenticated user cannot be found")
    void logout_userDoesNotExist_throws() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("ghost@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty()); // Simulate no matching user found

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThatThrownBy(() -> authService.logout())
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(refreshTokenService, never()).deleteAllByUser(any()); // Confirm nothing was cleaned up since there was no user
            verify(tokenBlacklistService, never()).blacklist(any());
            mockedContext.verify(SecurityContextHolder::clearContext, never()); // Confirm the context is left intact on failure
        }
    }
}