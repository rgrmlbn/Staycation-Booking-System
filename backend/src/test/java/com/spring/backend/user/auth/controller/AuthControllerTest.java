package com.spring.backend.user.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.backend.exception.DuplicateEmailException;
import com.spring.backend.module.shared.response.ApiResponse;
import com.spring.backend.module.shared.response.ApiResponseBuilder;
import com.spring.backend.module.user.auth.controller.AuthController;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.interfaces.AuthService;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.enums.Gender;
import com.spring.backend.module.user.user.enums.UserRole;
import com.spring.backend.security.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// addFilters = false skips running the security filter chain against test requests.
// JwtUtil, UserDetailsService, and TokenBlacklistService are still mocked because
// JwtFilter (a @Component) is instantiated during ApplicationContext startup
// regardless of whether its filter logic ever executes.
//
// apiResponseBuilder is mocked, so any test whose request triggers
// GlobalExceptionHandler must stub apiResponseBuilder.error(...) — otherwise it
// returns null and Spring MVC silently defaults to a 200 empty response.
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private ApiResponseBuilder apiResponseBuilder;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private RegisterRequest validRegisterRequest(String email) {
        return RegisterRequest.builder()
                .name("Roger Malabanan")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .contactNumber("9123456789")
                .address("123 Sample Street, Metro Manila")
                .email(email)
                .role(UserRole.GUEST)
                .password("StrongPass123!")
                .build();
    }

    @Test
    void register_validRequest_returns201() throws Exception {
        RegisterResponse response = RegisterResponse.builder()
                .email("newuser1@example.com")
                .build();

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest("newuser1@example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser1@example.com"));
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        when(authService.register(any()))
                .thenThrow(new DuplicateEmailException());

        when(apiResponseBuilder.error(eq(HttpStatus.CONFLICT), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder().build()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest("duplicate@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void register_blankEmail_returns400() throws Exception {
        RegisterRequest request = validRegisterRequest("");

        when(apiResponseBuilder.validationError(anyString(), any()))
                .thenReturn(ResponseEntity.badRequest().body(ApiResponse.builder().build()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_correctCredentials_returnsTokens() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .accessToken("fake-access-token")
                .refreshToken("fake-refresh-token")
                .build();

        LoginRequest login = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("StrongPass123!")
                .build();

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("fake-refresh-token"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("wrongpass@example.com")
                .password("WrongPassword!")
                .build();

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        when(apiResponseBuilder.error(eq(HttpStatus.UNAUTHORIZED), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().build()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_unknownEmail_returns401() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("doesnotexist@example.com")
                .password("Whatever123!")
                .build();

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        when(apiResponseBuilder.error(eq(HttpStatus.UNAUTHORIZED), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().build()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}