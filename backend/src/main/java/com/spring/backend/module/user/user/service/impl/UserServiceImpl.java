package com.spring.backend.module.user.user.service.impl;


import com.spring.backend.exception.*;
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

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getMe() {
        return userMapper.toResponse(ownershipVerifier.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateUserById(Long id, UpdateUserRequest update) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        ownershipVerifier.verifyOwnershipOrAdmin(user);

        if (update.getFirstName() != null && !update.getFirstName().isBlank()) {
            user.setFirstName(update.getFirstName());
        }

        if (update.getMiddleName() != null && !update.getMiddleName().isBlank()) {
            user.setMiddleName(update.getMiddleName());
        }

        if (update.getLastName() != null && !update.getLastName().isBlank()) {
            user.setLastName(update.getLastName());
        }

        if (update.getSuffix() != null && !update.getSuffix().isBlank()) {
            user.setSuffix(update.getSuffix());
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

        if (update.getStreet() != null && !update.getStreet().isBlank()) {
            user.setStreet(update.getStreet());
        }

        if (update.getCity() != null && !update.getCity().isBlank()) {
            user.setCity(update.getCity());
        }

        if (update.getProvince() != null && !update.getProvince().isBlank()) {
            user.setProvince(update.getProvince());
        }

        if (update.getPostalCode() != null && !update.getPostalCode().isBlank()) {
            user.setPostalCode(update.getPostalCode());
        }

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