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
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .contactNumber(request.getContactNumber())
                .street(request.getStreet())
                .barangay(request.getBarangay())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
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
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .contactNumber(user.getContactNumber())
                .street(user.getStreet())
                .barangay(user.getBarangay())
                .city(user.getCity())
                .province(user.getProvince())
                .postalCode(user.getPostalCode())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public RegisterResponse toRegisterResponse(UserEntity user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .contactNumber(user.getContactNumber())
                .street(user.getStreet())
                .barangay(user.getBarangay())
                .city(user.getCity())
                .province(user.getProvince())
                .postalCode(user.getPostalCode())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

}
