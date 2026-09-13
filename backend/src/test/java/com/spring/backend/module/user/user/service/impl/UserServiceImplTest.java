package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.exception.user.user.DuplicateEmailException;
import com.spring.backend.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.exception.user.user.PasswordMismatchException;
import com.spring.backend.exception.user.user.PasswordReuseException;
import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.Gender;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .name("Roger")
                .email("roger@example.com")
                .password("encoded-old-password")
                .build();
    }

    // ---------- getAllUsers() ----------

    @Test
    @DisplayName("Should return a mapped list of all users")
    void getAllUsers_returnsMappedList() {
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should return an empty list when there are no users")
    void getAllUsers_noUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    // ---------- getUserById() ----------

    @Test
    @DisplayName("Should return user when ID exists")
    void getUserById_userExists_returnsMappedUser() {
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
    void getUserById_userDoesNotExist_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userMapper, never()).toResponse(any());
    }

    // NOTE: getMe() removed from the service entirely.
    // "Who is the current user" now resolves in the controller via
    // @AuthenticationPrincipal, which then just calls getUserById(id) —
    // so there's no separate service method left to unit test here.

    // ---------- updateUserById() ----------

    @Test
    @DisplayName("Should update all fields when all are provided and valid")
    void updateUserById_allFieldsProvided_updatesAllFields() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);
        UserResponse response = mock(UserResponse.class);

        when(update.getName()).thenReturn("New Name");
        when(update.getGender()).thenReturn(Gender.valueOf("FEMALE"));
        when(update.getDateOfBirth()).thenReturn(LocalDate.parse("2000-01-01"));
        when(update.getContactNumber()).thenReturn("09171234567");
        when(update.getAddress()).thenReturn("123 New Street");
        when(update.getEmail()).thenReturn("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUserById(1L, update);

        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getContactNumber()).isEqualTo("09171234567");
        assertThat(user.getAddress()).isEqualTo("123 New Street");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(result).isEqualTo(response);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should leave fields unchanged when update values are null or blank")
    void updateUserById_blankOrNullFields_keepsOriginalValues() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getName()).thenReturn("   ");
        when(update.getGender()).thenReturn(null);
        when(update.getDateOfBirth()).thenReturn(null);
        when(update.getContactNumber()).thenReturn(null);
        when(update.getAddress()).thenReturn("   ");
        when(update.getEmail()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(mock(UserResponse.class));

        userService.updateUserById(1L, update);

        assertThat(user.getName()).isEqualTo("Roger");
        assertThat(user.getEmail()).isEqualTo("roger@example.com");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should not check for duplicates when the new email matches the current email")
    void updateUserById_sameEmail_skipsDuplicateCheck() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getEmail()).thenReturn("roger@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(mock(UserResponse.class));

        userService.updateUserById(1L, update);

        assertThat(user.getEmail()).isEqualTo("roger@example.com");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when the new email is already taken")
    void updateUserById_emailAlreadyTaken_throws() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getEmail()).thenReturn("taken@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUserById(1L, update))
                .isInstanceOf(DuplicateEmailException.class);

        assertThat(user.getEmail()).isEqualTo("roger@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a user that does not exist")
    void updateUserById_userDoesNotExist_throws() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserById(1L, update))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    // ---------- changePasswordById() ----------

    @Test
    @DisplayName("Should change the password when the current password matches and the new password is valid")
    void changePasswordById_validRequest_changesPassword() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(request.getCurrentPassword()).thenReturn("old-password");
        when(request.getNewPassword()).thenReturn("new-password");
        when(request.getConfirmPassword()).thenReturn("new-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("new-password", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        userService.changePasswordById(1L, request);

        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        verify(refreshTokenService).deleteAllByUser(user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw IncorrectCurrentPasswordException when the current password is wrong")
    void changePasswordById_wrongCurrentPassword_throws() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(request.getCurrentPassword()).thenReturn("wrong-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePasswordById(1L, request))
                .isInstanceOf(IncorrectCurrentPasswordException.class);

        verify(refreshTokenService, never()).deleteAllByUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PasswordMismatchException when new and confirm passwords differ")
    void changePasswordById_passwordsDoNotMatch_throws() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(request.getCurrentPassword()).thenReturn("old-password");
        when(request.getNewPassword()).thenReturn("new-password");
        when(request.getConfirmPassword()).thenReturn("different-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.changePasswordById(1L, request))
                .isInstanceOf(PasswordMismatchException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PasswordReuseException when the new password matches the current one")
    void changePasswordById_newPasswordIsSameAsCurrent_throws() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(request.getCurrentPassword()).thenReturn("old-password");
        when(request.getNewPassword()).thenReturn("old-password");
        when(request.getConfirmPassword()).thenReturn("old-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.changePasswordById(1L, request))
                .isInstanceOf(PasswordReuseException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when changing the password of a user that does not exist")
    void changePasswordById_userDoesNotExist_throws() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePasswordById(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    // ---------- deleteUserById() ----------

    @Test
    @DisplayName("Should delete user and their refresh tokens when ID exists")
    void deleteUserById_userExists_deletesUserAndTokens() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(refreshTokenService).deleteAllByUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting a user that does not exist")
    void deleteUserById_userDoesNotExist_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(refreshTokenService, never()).deleteAllByUser(any());
        verify(userRepository, never()).delete(any());
    }
}