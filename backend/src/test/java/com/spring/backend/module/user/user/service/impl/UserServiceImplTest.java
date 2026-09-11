package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.exception.user.user.DuplicateEmailException;
import com.spring.backend.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.exception.user.user.PasswordMismatchException;
import com.spring.backend.exception.user.user.PasswordReuseException;
import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.module.shared.util.OwnershipVerifier;
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
    private UserRepository userRepository; // Fake repository — no real database involved

    @Mock
    private UserMapper userMapper; // Fake mapper — controls exactly what DTO conversion returns

    @Mock
    private PasswordEncoder passwordEncoder; // Fake encoder — controls match/encode results directly

    @Mock
    private RefreshTokenService refreshTokenService; // Fake token service — confirms it's invoked without needing real tokens

    @Mock
    private OwnershipVerifier ownershipVerifier; // Fake ownership check — lets us simulate allowed/denied access

    @InjectMocks
    private UserServiceImpl userService; // Real service, wired with all mocks above

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

        when(userRepository.findAll()).thenReturn(List.of(user)); // Simulate one user existing in the "database"
        when(userMapper.toResponse(user)).thenReturn(response); // Simulate the mapper converting it to a DTO

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1); // Confirm the list has exactly the one user
        assertThat(result.get(0)).isEqualTo(response); // Confirm it's the mapped DTO, not the raw entity
    }

    @Test
    @DisplayName("Should return an empty list when there are no users")
    void getAllUsers_noUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of()); // Simulate an empty database

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty(); // Confirm no users means no results, not a null or an exception
    }

    // ---------- getUserById() ----------

    @Test
    @DisplayName("Should return user when ID exists")
    void getUserById_userExists_returnsMappedUser() {
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the user
        when(userMapper.toResponse(user)).thenReturn(response); // Simulate mapping it to a DTO

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isEqualTo(response); // Confirm the returned DTO matches what the mapper produced
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
    void getUserById_userDoesNotExist_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); // Simulate no user found

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class); // Expect the service to reject a missing ID

        verify(userMapper, never()).toResponse(any()); // Confirm mapping was never attempted since there was nothing to map
    }

    // ---------- getMe() ----------

    @Test
    @DisplayName("Should return the mapped currently authenticated user")
    void getMe_returnsMappedCurrentUser() {
        UserResponse response = mock(UserResponse.class);

        when(ownershipVerifier.getCurrentUser()).thenReturn(user); // Simulate resolving the logged-in user
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getMe();

        assertThat(result).isEqualTo(response); // Confirm the returned DTO matches what the mapper produced
    }

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the existing user
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false); // Simulate the new email being available
        when(userRepository.save(user)).thenReturn(user); // Simulate persisting the updated entity
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUserById(1L, update);

        assertThat(user.getName()).isEqualTo("New Name"); // Confirm each field was actually changed
        assertThat(user.getContactNumber()).isEqualTo("09171234567");
        assertThat(user.getAddress()).isEqualTo("123 New Street");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(result).isEqualTo(response);
        verify(ownershipVerifier).verifyOwnershipOrAdmin(user); // Confirm the caller's ownership/admin status was checked
        verify(userRepository).save(user); // Confirm the updated entity was persisted
    }

    @Test
    @DisplayName("Should leave fields unchanged when update values are null or blank")
    void updateUserById_blankOrNullFields_keepsOriginalValues() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getName()).thenReturn("   "); // Blank, should be ignored
        when(update.getGender()).thenReturn(null);
        when(update.getDateOfBirth()).thenReturn(null);
        when(update.getContactNumber()).thenReturn(null);
        when(update.getAddress()).thenReturn("   "); // Blank, should be ignored
        when(update.getEmail()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(mock(UserResponse.class));

        userService.updateUserById(1L, update);

        assertThat(user.getName()).isEqualTo("Roger"); // Confirm nothing changed
        assertThat(user.getEmail()).isEqualTo("roger@example.com");
        verify(userRepository, never()).existsByEmail(anyString()); // No email lookup since the email was never touched
    }

    @Test
    @DisplayName("Should not check for duplicates when the new email matches the current email")
    void updateUserById_sameEmail_skipsDuplicateCheck() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getEmail()).thenReturn("roger@example.com"); // Same as the user's current email
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(mock(UserResponse.class));

        userService.updateUserById(1L, update);

        assertThat(user.getEmail()).isEqualTo("roger@example.com"); // Still set, just re-assigned to itself
        verify(userRepository, never()).existsByEmail(anyString()); // No duplicate check needed since the email didn't change
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when the new email is already taken")
    void updateUserById_emailAlreadyTaken_throws() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(update.getEmail()).thenReturn("taken@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true); // Simulate another user already owning this email

        assertThatThrownBy(() -> userService.updateUserById(1L, update))
                .isInstanceOf(DuplicateEmailException.class);

        assertThat(user.getEmail()).isEqualTo("roger@example.com"); // Confirm the email was never overwritten
        verify(userRepository, never()).save(any()); // Confirm nothing was saved once the conflict was detected
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a user that does not exist")
    void updateUserById_userDoesNotExist_throws() {
        UpdateUserRequest update = mock(UpdateUserRequest.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserById(1L, update))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ownershipVerifier, never()).verifyOwnershipOrAdmin(any()); // Confirm ownership was never checked since there was no user
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Simulate finding the existing user
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true); // Simulate a correct current password
        when(passwordEncoder.matches("new-password", user.getPassword())).thenReturn(false); // Simulate the new password not being a reuse
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password"); // Simulate hashing the new password

        userService.changePasswordById(1L, request);

        assertThat(user.getPassword()).isEqualTo("encoded-new-password"); // Confirm the password was actually replaced
        verify(ownershipVerifier).verifyOwnershipOrAdmin(user); // Confirm the caller's ownership/admin status was checked
        verify(refreshTokenService).deleteAllByUser(user); // Confirm existing sessions were invalidated
        verify(userRepository).save(user); // Confirm the updated entity was persisted
    }

    @Test
    @DisplayName("Should throw IncorrectCurrentPasswordException when the current password is wrong")
    void changePasswordById_wrongCurrentPassword_throws() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);

        when(request.getCurrentPassword()).thenReturn("wrong-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false); // Simulate a failed password check

        assertThatThrownBy(() -> userService.changePasswordById(1L, request))
                .isInstanceOf(IncorrectCurrentPasswordException.class);

        verify(refreshTokenService, never()).deleteAllByUser(any()); // Confirm no sessions were touched
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
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true); // Matches for both the current-password check and the reuse check

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

        verify(ownershipVerifier, never()).verifyOwnershipOrAdmin(any());
        verify(userRepository, never()).save(any());
    }

    // ---------- deleteUserById() ----------

    @Test
    @DisplayName("Should delete user and their refresh tokens when ID exists")
    void deleteUserById_userExists_deletesUserAndTokens() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(ownershipVerifier).verifyOwnershipOrAdmin(user); // Confirm the caller's ownership/admin status was checked
        verify(refreshTokenService).deleteAllByUser(user); // Confirm the user's sessions were cleaned up
        verify(userRepository).delete(user); // Confirm the correct entity was passed to delete
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting a user that does not exist")
    void deleteUserById_userDoesNotExist_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(refreshTokenService, never()).deleteAllByUser(any()); // Confirm nothing was cleaned up since there was no user
        verify(userRepository, never()).delete(any());
    }
}