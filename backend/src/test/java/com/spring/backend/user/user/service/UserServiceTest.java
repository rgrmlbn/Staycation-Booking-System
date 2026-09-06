package com.spring.backend.user.user.service;

import com.spring.backend.exception.*;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private OwnershipVerifier ownershipVerifier; // Mocked so ownership/admin checks can be simulated instead of running real security logic

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setPassword("encodedOldPassword"); // Simulates a user whose password is already hashed, as it would be in the database

        // Built via @Builder since ChangePasswordRequest has no setters
        changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("oldPassword") // The plain-text password the "user" claims is their current one
                .newPassword("newPassword123") // The plain-text password they want to switch to
                .confirmPassword("newPassword123") // Confirmation field — matches newPassword by default for the happy path
                .build();
    }

    @Test
    void changePassword_wrongCurrentPassword_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user by ID
        doNothing().when(ownershipVerifier).verifyOwnershipOrAdmin(user); // Let the ownership check pass — not what this test is targeting
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(false); // Simulate the entered "current password" NOT matching what's stored

        assertThatThrownBy(() -> userService.changePasswordById(1L, changePasswordRequest))
                .isInstanceOf(IncorrectCurrentPasswordException.class); // Expect rejection since the current password was wrong

        verify(userRepository, never()).save(any()); // Confirm nothing was persisted since the change was rejected
    }

    @Test
    void changePassword_newAndConfirmMismatch_throws() {
        // No setters available, so rebuild the request with a deliberately mismatched confirmPassword
        changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user by ID
        doNothing().when(ownershipVerifier).verifyOwnershipOrAdmin(user); // Let the ownership check pass — not what this test is targeting
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true); // Current password check succeeds, so mismatch is the only failure being tested

        assertThatThrownBy(() -> userService.changePasswordById(1L, changePasswordRequest))
                .isInstanceOf(PasswordMismatchException.class); // Expect rejection since newPassword and confirmPassword don't match
    }

    @Test
    void changePassword_reusingOldPassword_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user by ID
        doNothing().when(ownershipVerifier).verifyOwnershipOrAdmin(user); // Let the ownership check pass — not what this test is targeting
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true); // Current password is correct
        when(passwordEncoder.matches("newPassword123", "encodedOldPassword")).thenReturn(true); // Simulate the "new" password actually being identical to the old one

        assertThatThrownBy(() -> userService.changePasswordById(1L, changePasswordRequest))
                .isInstanceOf(PasswordReuseException.class); // Expect rejection since you can't "change" to the same password
    }

    @Test
    void changePassword_success_updatesAndRevokesRefreshTokens() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user by ID
        doNothing().when(ownershipVerifier).verifyOwnershipOrAdmin(user); // Ownership check passes
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true); // Current password is correct
        when(passwordEncoder.matches("newPassword123", "encodedOldPassword")).thenReturn(false); // New password is different from the old one, so reuse check passes
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword"); // Simulate hashing the new password before saving

        userService.changePasswordById(1L, changePasswordRequest); // Run the actual password change

        verify(refreshTokenService).deleteAllByUser(user); // Confirm all existing sessions/refresh tokens are revoked after a password change (security best practice)
        verify(userRepository).save(user); // Confirm the updated user was persisted
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword"); // Confirm the entity's password field was actually updated to the newly encoded value
    }

    @Test
    void deleteUser_notOwnerNorAdmin_throwsAndNeverDeletes() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user by ID
        doThrow(new AccessDeniedException("Not allowed"))
                .when(ownershipVerifier).verifyOwnershipOrAdmin(user); // Simulate the caller NOT being the owner or an admin

        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(AccessDeniedException.class); // Expect the deletion to be blocked

        verify(userRepository, never()).delete(any()); // Confirm the user was never actually deleted
        verify(refreshTokenService, never()).deleteAllByUser(any()); // Confirm no session cleanup happened either, since the action never went through
    }
}