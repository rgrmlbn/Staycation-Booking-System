package com.spring.backend.module.user.user.mapper;

import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(RegisterRequest request);

    @Mapping(target = "age", expression = "java(calculateAge(user.getDateOfBirth()))")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(UserEntity user);

    @Mapping(target = "age", expression = "java(calculateAge(user.getDateOfBirth()))")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    RegisterResponse toRegisterResponse(UserEntity user);

    default int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}