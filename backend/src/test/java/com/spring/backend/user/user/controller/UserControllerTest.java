package com.spring.backend.user.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.backend.exception.ResourceNotFoundException;
import com.spring.backend.module.shared.response.ApiResponse;
import com.spring.backend.module.shared.response.ApiResponseBuilder;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.controller.UserController;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserService;
import com.spring.backend.security.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Security filters disabled here on purpose — this class tests controller
// wiring, path-variable validation, and request-body validation only.
// Role/auth enforcement is tested separately in UserControllerSecurityIT.
//
// JwtUtil, UserDetailsService, and TokenBlacklistService are mocked because
// JwtFilter (a @Component) is instantiated during ApplicationContext startup
// regardless of whether its filter logic ever executes (addFilters=false only
// stops it from running against requests, not from being constructed).
//
// apiResponseBuilder is mocked, so any test whose request triggers
// GlobalExceptionHandler must stub apiResponseBuilder's relevant method —
// otherwise it returns null and Spring MVC silently defaults to a 200 empty response.
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private ApiResponseBuilder apiResponseBuilder;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private TokenBlacklistService tokenBlacklistService;

    private UserResponse sampleUserResponse(Long id) {
        return UserResponse.builder()
                .id(id)
                .email("user" + id + "@example.com")
                .name("Roger Malabanan")
                .build();
    }

    // ---------- GET /users ----------

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(
                List.of(sampleUserResponse(1L), sampleUserResponse(2L))
        );

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(userService).getAllUsers();
    }

    // ---------- GET /users/me ----------

    @Test
    void getCurrentUser_returnsCallerProfile() throws Exception {
        when(userService.getMe()).thenReturn(sampleUserResponse(5L));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value("user5@example.com"));
    }


    // ---------- GET /users/{id} ----------

    @Test
    void getUserById_validId_returnsUser() throws Exception {
        when(userService.getUserById(3L)).thenReturn(sampleUserResponse(3L));

        mockMvc.perform(get("/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void getUserById_nonPositiveId_returns400() throws Exception {
        // NOTE: if this still returns 200/500 instead of 400, the exception thrown
        // by @Positive validation (likely ConstraintViolationException) isn't
        // explicitly handled in GlobalExceptionHandler and falls to the generic
        // Exception handler. Check which exception type is actually resolved
        // (see "Resolved Exception" in the MockMvc failure output) and stub accordingly.
        when(apiResponseBuilder.error(eq(HttpStatus.BAD_REQUEST), anyString()))
                .thenReturn(ResponseEntity.badRequest().body(ApiResponse.builder().build()));

        mockMvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/users/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User"));

        when(apiResponseBuilder.error(eq(HttpStatus.NOT_FOUND), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().build()));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    // ---------- PATCH /users/{id} ----------

    @Test
    void updateUserById_validRequest_returnsUpdatedUser() throws Exception {
        // Built via @Builder — assumes UpdateUserRequest follows the same no-setter pattern as your other DTOs.
        // If it actually has @Setter, swap this back to new UpdateUserRequest() + setFirstName(...).
        UpdateUserRequest update = UpdateUserRequest.builder()
                .name("UpdatedName")
                .build();

        when(userService.updateUserById(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(sampleUserResponse(1L));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateUserById_nonPositiveId_returns400() throws Exception {
        UpdateUserRequest update = UpdateUserRequest.builder()
                .name("Name")
                .build();

        when(apiResponseBuilder.error(eq(HttpStatus.BAD_REQUEST), anyString()))
                .thenReturn(ResponseEntity.badRequest().body(ApiResponse.builder().build()));

        mockMvc.perform(patch("/users/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    // ---------- PATCH /users/{id}/change-password ----------

    @Test
    void changePasswordById_validRequest_returns204() throws Exception {
        // Built via @Builder since ChangePasswordRequest has no setters
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPass123")
                .newPassword("newPass456!")
                .confirmPassword("newPass456!")
                .build();

        doNothing().when(userService).changePasswordById(eq(1L), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).changePasswordById(eq(1L), any(ChangePasswordRequest.class));
    }

    @Test
    void changePasswordById_missingFields_returns400() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder().build();

        when(apiResponseBuilder.error(eq(HttpStatus.BAD_REQUEST), anyString()))
                .thenReturn(ResponseEntity.badRequest().body(ApiResponse.builder().build()));

        mockMvc.perform(patch("/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
    // ---------- DELETE /users/{id} ----------

    @Test
    void deleteUserById_validId_returns204() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void deleteUserById_nonPositiveId_returns400() throws Exception {
        when(apiResponseBuilder.error(eq(HttpStatus.BAD_REQUEST), anyString()))
                .thenReturn(ResponseEntity.badRequest().body(ApiResponse.builder().build()));

        mockMvc.perform(delete("/users/0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}