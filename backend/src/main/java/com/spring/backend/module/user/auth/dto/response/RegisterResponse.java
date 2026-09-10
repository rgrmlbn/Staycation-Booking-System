package com.spring.backend.module.user.auth.dto.response;

import com.spring.backend.module.user.user.enums.Gender;
import com.spring.backend.module.user.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RegisterResponse {

    private Long id;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
    private int age;
    private String contactNumber;
    private String address;
    private String email;
    private UserRole role;
}
