package com.spring.backend.module.user.user.mapper;

import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserMapper {

    public UserEntity toEntity(RegisterRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public UserResponse toResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .contactNumber(user.getContactNumber())
                .address(user.getAddress())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public RegisterResponse toRegisterResponse(UserEntity user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .contactNumber(user.getContactNumber())
                .address(user.getAddress())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

}