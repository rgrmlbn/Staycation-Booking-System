package com.spring.backend.user.auth.service;

import com.spring.backend.exception.DuplicateEmailException;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RefreshTokenRequest;

import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.impl.AuthServiceImpl;
import com.spring.backend.module.user.auth.service.interfaces.LoginRateLimiterService;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.Gender;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.security.principal.UserPrincipal;
import com.spring.backend.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private TokenBlacklistService tokenBlacklistService;
    @Mock private LoginRateLimiterService loginRateLimiterService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        // Built via @Builder instead of setters — fill in every field the validation annotations require
        registerRequest = RegisterRequest.builder()
                .name("Roger Malabanan")
                .gender(Gender.MALE) // adjust to whatever enum value actually exists
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .contactNumber("9123456789")
                .address("123 Sample Street, Metro Manila")
                .email("test@example.com")
                .password("plainPassword123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("plainPassword123")
                .build();

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
    }

    // ---------- register() ----------

    @Test
    void register_success_encodesPasswordAndSaves() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(userEntity);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toRegisterResponse(userEntity)).thenReturn(
                RegisterResponse.builder()
                        .email("test@example.com").build()
        );

        RegisterResponse response = authService.register(registerRequest);

        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(passwordEncoder).encode("plainPassword123");
        verify(userRepository).save(userEntity);
        assertThat(userEntity.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void register_duplicateEmail_throwsAndNeverSaves() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any());
    }

    // ---------- login() ----------

    @Test
    void login_success_returnsTokens() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(userEntity);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtUtil.generateAccessToken(principal)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(userEntity)).thenReturn("refresh-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        // rate limiter must be checked BEFORE authentication is attempted
        InOrder order = inOrder(loginRateLimiterService, authenticationManager);
        order.verify(loginRateLimiterService).checkLimits(loginRequest.getEmail());
        order.verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_rateLimited_neverCallsAuthenticationManager() {
        doThrow(new RuntimeException("Too many attempts"))
                .when(loginRateLimiterService).checkLimits(anyString());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void login_badCredentials_propagatesException() {
        doNothing().when(loginRateLimiterService).checkLimits(anyString());
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtUtil);
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    // ---------- refreshToken() ----------

    @Test
    void refreshToken_valid_rotatesAndIssuesNewAccessToken() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("old-refresh-token")
                .build();

        RefreshToken oldToken = mock(RefreshToken.class);
        when(oldToken.getUser()).thenReturn(userEntity);
        when(refreshTokenService.validateRefreshToken("old-refresh-token")).thenReturn(oldToken);
        when(refreshTokenService.rotateRefreshToken(oldToken)).thenReturn("new-refresh-token");
        when(jwtUtil.generateAccessToken(any(UserPrincipal.class))).thenReturn("new-access-token");

        AuthResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }
}