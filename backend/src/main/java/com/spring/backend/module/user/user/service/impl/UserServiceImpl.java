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
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final OwnershipVerifier ownershipVerifier;

    // Get all users
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // Get a user by its ID, throwing an exception if not found
    @Override
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        return userMapper.toResponse(user);
    }

    // Get the currently authenticated user
    @Override
    public UserResponse getMe() {
        return userMapper.toResponse(ownershipVerifier.getCurrentUser());
    }

    // Update a user by its ID, verifying ownership or admin rights before updating
    @Override
    @Transactional
    public UserResponse updateUserById(Long id, UpdateUserRequest update) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        ownershipVerifier.verifyOwnershipOrAdmin(user);

        // Update fields if they are not null or blank
        if (update.getName() != null && !update.getName().isBlank()) {
            user.setName(update.getName());
        }

        if (update.getGender() != null) {
            user.setGender(update.getGender());
        }

        if (update.getDateOfBirth() != null) {
            user.setDateOfBirth(update.getDateOfBirth());
        }

        if (update.getContactNumber() != null && !update.getContactNumber().isBlank()) {
            user.setContactNumber(update.getContactNumber());
        }

        if (update.getAddress() != null && !update.getAddress().isBlank()) {
            user.setAddress(update.getAddress());
        }

        // Update email if it is not null or blank, checking for duplicate emails
        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            if (!user.getEmail().equals(update.getEmail()) &&
                    userRepository.existsByEmail(update.getEmail())) {
                throw new DuplicateEmailException();
            }
            user.setEmail(update.getEmail());
        }

        UserEntity updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // Change a user's password by ID, verifying ownership or admin rights and validating the current password
    @Override
    @Transactional
    public void changePasswordById(Long id, ChangePasswordRequest request) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        ownershipVerifier.verifyOwnershipOrAdmin(user);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectCurrentPasswordException();
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new PasswordReuseException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        refreshTokenService.deleteAllByUser(user);

        userRepository.save(user);
    }

    // Delete a user by its ID, verifying ownership or admin rights before deletion
    @Override
    @Transactional
    public void deleteUserById(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        ownershipVerifier.verifyOwnershipOrAdmin(user);

        refreshTokenService.deleteAllByUser(user);
        userRepository.delete(user);

    }
}